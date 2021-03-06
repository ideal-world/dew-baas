/*
 * Copyright 2021. gudaoxuri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package idealworld.dew.serviceless.iam.exchange;

import idealworld.dew.framework.DewConstant;
import idealworld.dew.framework.dto.CommonStatus;
import idealworld.dew.framework.dto.OptActionKind;
import idealworld.dew.framework.exception.BadRequestException;
import idealworld.dew.framework.fun.auth.dto.*;
import idealworld.dew.framework.fun.eventbus.EventBusProcessor;
import idealworld.dew.framework.fun.eventbus.ProcessContext;
import idealworld.dew.framework.util.URIHelper;
import idealworld.dew.serviceless.iam.IAMConstant;
import idealworld.dew.serviceless.iam.domain.auth.Resource;
import idealworld.dew.serviceless.iam.domain.auth.ResourceSubject;
import idealworld.dew.serviceless.iam.domain.ident.App;
import idealworld.dew.serviceless.iam.domain.ident.AppIdent;
import idealworld.dew.serviceless.iam.domain.ident.Tenant;
import idealworld.dew.serviceless.iam.process.IAMBasicProcessor;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限策略交互处理器.
 *
 * @author gudaoxuri
 */
@Slf4j
public class ExchangeProcessor extends EventBusProcessor {

    {
        addProcessor(OptActionKind.FETCH, DewConstant.REQUEST_INNER_PATH_PREFIX + "resource/subject", eventBusContext ->
                findResourceSubjects(eventBusContext.req.params.getOrDefault("kind", null), eventBusContext.context));
        addProcessor(OptActionKind.FETCH, DewConstant.REQUEST_INNER_PATH_PREFIX + "resource", eventBusContext ->
                findResources(eventBusContext.req.params.getOrDefault("kind", null), eventBusContext.context));
    }

    public ExchangeProcessor(String moduleName) {
        super(moduleName);
    }

    private static Future<List<ResourceSubjectExchange>> findResourceSubjects(String kind, ProcessContext context) {
        var sql = "SELECT * FROM %s";
        var whereParameters = new HashMap<String, Object>();
        if (kind != null && !kind.isBlank()) {
            sql += " WHERE kind = #{kind}";
            whereParameters.put("kind", kind);
        }
        return context.sql.list(
                String.format(sql, new ResourceSubject().tableName()),
                whereParameters)
                .compose(resourceSubjects ->
                        context.helper.success(
                                resourceSubjects.stream()
                                        .map(resourceSubject ->
                                                ResourceSubjectExchange.builder()
                                                        .code(resourceSubject.getString("code"))
                                                        .name(resourceSubject.getString("name"))
                                                        .kind(ResourceKind.parse(resourceSubject.getString("kind")))
                                                        .uri(resourceSubject.getString("uri"))
                                                        .ak(resourceSubject.getString("ak"))
                                                        .sk(resourceSubject.getString("sk"))
                                                        .platformAccount(resourceSubject.getString("platform_account"))
                                                        .platformProjectId(resourceSubject.getString("platform_project_id"))
                                                        .timeoutMs(resourceSubject.getLong("timeout_ms"))
                                                        .build())
                                        .collect(Collectors.toList())));
    }

    private static Future<List<ResourceExchange>> findResources(String kind, ProcessContext context) {
        var sql = "SELECT resource.uri, resource.action FROM %s resource";
        var whereParameters = new HashMap<String, Object>();
        if (kind != null && !kind.isBlank()) {
            sql += " INNER JOIN %s subject ON subject.id = resource.rel_resource_subject_id" +
                    " WHERE subject.kind = #{kind}";
            sql = String.format(sql, new Resource().tableName(), new ResourceSubject().tableName());
            whereParameters.put("kind", kind);
        } else {
            sql = String.format(sql, new Resource().tableName());
        }
        return context.sql.list(
                sql,
                whereParameters)
                .compose(resources ->
                        context.helper.success(
                                resources.stream()
                                        .map(resource ->
                                                ResourceExchange.builder()
                                                        .uri(resource.getString("uri"))
                                                        .actionKind(resource.getString("action"))
                                                        .build())
                                        .collect(Collectors.toList())));
    }

    public static void publish(OptActionKind actionKind, String subjectCategory, Object subjectId, Object detailData, ProcessContext context) {
        context.eb.publish("",
                actionKind,
                "eb://" + context.moduleName + "/" + subjectCategory + "/" + subjectId,
                JsonObject.mapFrom(detailData).toBuffer(),
                new HashMap<>());
    }

    public static Future<Void> enableTenant(Long tenantId, ProcessContext context) {
        return context.sql.list(
                String.format("SELECT ident.ak, ident.sk, ident.rel_app_id, app.open_id, ident.valid_time FROM %s AS ident" +
                                " INNER JOIN %s AS app ON app.id = ident.rel_app_id AND app.status = #{status}" +
                                " WHERE app.rel_tenant_id = #{rel_tenant_id} AND ident.valid_time > #{valid_time}",
                        new AppIdent().tableName(), new App().tableName()),
                new HashMap<>() {
                    {
                        put("status", CommonStatus.ENABLED);
                        put("rel_tenant_id", tenantId);
                        put("valid_time", System.currentTimeMillis());
                    }
                })
                .compose(appIdents ->
                        CompositeFuture.all(appIdents.stream()
                                .map(appIdent -> {
                                    var ak = appIdent.getString("ak");
                                    var sk = appIdent.getString("sk");
                                    var appId = appIdent.getLong("rel_app_id");
                                    var appCode = appIdent.getString("open_id");
                                    var validTime = appIdent.getLong("valid_time");
                                    return changeAppIdent(ak, sk, validTime, appId, appCode, tenantId, context);
                                })
                                .collect(Collectors.toList())))
                .compose(resp -> context.helper.success());
    }

    public static Future<Void> disableTenant(Long tenantId, ProcessContext context) {
        return context.sql.list(
                String.format("SELECT ident.ak FROM %s AS ident" +
                        " INNER JOIN %s app ON app.id = ident.rel_app_id" +
                        " WHERE app.rel_tenant_id = #{rel_tenant_id}", new AppIdent().tableName(), new App().tableName()),
                new HashMap<>() {
                    {
                        put("rel_tenant_id", tenantId);
                    }
                })
                .compose(appIdents ->
                        CompositeFuture.all(appIdents.stream()
                                .map(appIdent -> {
                                    var ak = appIdent.getString("ak");
                                    return deleteAppIdent(ak, context);
                                })
                                .collect(Collectors.toList())))
                .compose(resp -> context.helper.success());
    }

    public static Future<Void> enableApp(String appCode, Long appId, Long tenantId, ProcessContext context) {
        return context.sql.getOne(
                new HashMap<>() {
                    {
                        put("id", appId);
                    }
                },
                App.class)
                .compose(app -> {
                    var publicKey = app.getPubKey();
                    var privateKey = app.getPriKey();
                    return context.cache.set(IAMConstant.CACHE_APP_INFO + appCode, appId + "\n" + tenantId + "\n" + publicKey + "\n" + privateKey);
                })
                .compose(resp ->
                        context.sql.list(
                                String.format("SELECT ak, sk, valid_time FROM %s" +
                                        " WHERE rel_app_id = #{rel_app_id} AND valid_time > #{valid_time}", new AppIdent().tableName()),
                                new HashMap<>() {
                                    {
                                        put("rel_app_id", appId);
                                        put("valid_time", System.currentTimeMillis());
                                    }
                                }))
                .compose(appIdents ->
                        CompositeFuture.all(appIdents.stream()
                                .map(appIdent -> {
                                    var ak = appIdent.getString("ak");
                                    var sk = appIdent.getString("sk");
                                    var validTime = appIdent.getLong("valid_time");
                                    return changeAppIdent(ak, sk, validTime, appId, appCode, tenantId, context);
                                })
                                .collect(Collectors.toList())))
                .compose(resp -> context.helper.success());
    }

    public static Future<Void> disableApp(String appCode, Long appId, Long tenantId, ProcessContext context) {
        return context.sql.list(
                new HashMap<>() {
                    {
                        put("rel_app_id", appId);
                    }
                },
                AppIdent.class)
                .compose(appIdents ->
                        CompositeFuture.all(appIdents.stream().map(appIdent -> deleteAppIdent(appIdent.getAk(), context)).collect(Collectors.toList()))
                )
                .compose(resp -> context.cache.del(IAMConstant.CACHE_APP_INFO + appCode));
    }

    public static Future<Void> changeAppIdent(AppIdent appIdent, Long appId, Long tenantId, ProcessContext context) {
        return IAMBasicProcessor.getAppCodeById(appId, tenantId, context)
                .compose(appCode ->
                        changeAppIdent(appIdent.getAk(), appIdent.getSk(), appIdent.getValidTime(), appId, appCode, tenantId, context));
    }

    private static Future<Void> changeAppIdent(String ak, String sk, Long validTime, Long appId, String appCode, Long tenantId,
                                               ProcessContext context) {
        return context.cache.del(IAMConstant.CACHE_APP_AK + ak)
                .compose(resp -> {
                    if (validTime == null) {
                        return context.cache.set(IAMConstant.CACHE_APP_AK + ak, sk + ":" + tenantId + ":" + appId + ":" + appCode);
                    } else {
                        return context.cache.setex(IAMConstant.CACHE_APP_AK + ak, sk + ":" + tenantId + ":" + appId + ":" + appCode,
                                (validTime - System.currentTimeMillis()) / 1000);
                    }
                });
    }

    public static Future<Void> deleteAppIdent(String ak, ProcessContext context) {
        return context.cache.del(IAMConstant.CACHE_APP_AK + ak);
    }

    public static Future<Void> addPolicy(AuthPolicyInfo policyInfo, ProcessContext context) {
        if ((policyInfo.getSubjectOperator() == AuthSubjectOperatorKind.INCLUDE
                || policyInfo.getSubjectOperator() == AuthSubjectOperatorKind.LIKE)
                && policyInfo.subjectKind != AuthSubjectKind.GROUP_NODE) {
            context.helper.error(new BadRequestException("权限主体运算类型为INCLUDE/LIKE时权限主体只能为GROUP_NODE"));
        }
        policyInfo.setResourceUri(URIHelper.formatUri(policyInfo.getResourceUri()));
        var key = IAMConstant.CACHE_AUTH_POLICY
                + policyInfo.getResourceUri().replace("//", "") + ":"
                + policyInfo.getActionKind().toString().toLowerCase();
        return context.cache.get(key)
                .compose(strPolicyValue -> {
                    var policyValue = strPolicyValue != null ? new JsonObject(strPolicyValue) : new JsonObject();
                    if (!policyValue.containsKey(policyInfo.subjectOperator.toString().toLowerCase())) {
                        policyValue.put(policyInfo.subjectOperator.toString().toLowerCase(), new JsonObject());
                    }
                    if (!policyValue.getJsonObject(policyInfo.subjectOperator.toString().toLowerCase())
                            .containsKey(policyInfo.subjectKind.toString().toLowerCase())) {
                        policyValue.getJsonObject(policyInfo.subjectOperator.toString().toLowerCase())
                                .put(policyInfo.subjectKind.toString().toLowerCase(), new JsonArray());
                    }
                    policyValue.getJsonObject(policyInfo.subjectOperator.toString().toLowerCase())
                            .getJsonArray(policyInfo.subjectKind.toString().toLowerCase()).add(policyInfo.getSubjectId());
                    return context.cache.set(key, policyValue.toString());
                });
    }

    public static Future<Void> removePolicy(AuthPolicyInfo policyInfo, ProcessContext context) {
        policyInfo.setResourceUri(URIHelper.formatUri(policyInfo.getResourceUri()));
        var key = IAMConstant.CACHE_AUTH_POLICY
                + policyInfo.getResourceUri().replace("//", "") + ":"
                + policyInfo.getActionKind().toString().toLowerCase();
        return context.cache.get(key)
                .compose(strPolicyValue -> {
                    if (strPolicyValue == null) {
                        return context.helper.success();
                    }
                    var policyValue = new JsonObject(strPolicyValue);
                    if (!policyValue.containsKey(policyInfo.subjectOperator.toString().toLowerCase())
                            || !policyValue.getJsonObject(policyInfo.subjectOperator.toString().toLowerCase())
                            .containsKey(policyInfo.subjectKind.toString().toLowerCase())) {
                        return context.helper.success();
                    }
                    policyValue.getJsonObject(policyInfo.subjectOperator.toString().toLowerCase())
                            .getJsonArray(policyInfo.subjectKind.toString().toLowerCase()).remove(policyInfo.getSubjectId());
                    return context.cache.set(key, policyValue.toString());
                });
    }

    public static Future<Void> removePolicy(String resourceUri, OptActionKind actionKind, ProcessContext context) {
        resourceUri = URIHelper.formatUri(resourceUri);
        var key = IAMConstant.CACHE_AUTH_POLICY
                + resourceUri.replace("//", "") + ":"
                + actionKind.toString().toLowerCase();
        return context.cache.del(key);
    }

    public Future<Void> init(ProcessContext context) {
        return cacheAppIdents(context);
    }

    private Future<Void> cacheAppIdents(ProcessContext context) {
        if (!context.cache.isLeader()) {
            return context.helper.success();
        }
        return context.sql.list(
                String.format("SELECT ident.ak, ident.sk, ident.rel_app_id,app.open_id, ident.valid_time, app.rel_tenant_id FROM %s AS ident" +
                                " INNER JOIN %s AS app ON app.id = ident.rel_app_id AND app.status = #{status}" +
                                " INNER JOIN %s AS tenant ON tenant.id = app.rel_tenant_id AND tenant.status = #{status}" +
                                " WHERE ident.valid_time > #{valid_time}",
                        new AppIdent().tableName(), new App().tableName(), new Tenant().tableName()),
                new HashMap<>() {
                    {
                        put("status", CommonStatus.ENABLED);
                        put("valid_time", System.currentTimeMillis());
                    }
                })
                .compose(appIdents ->
                        CompositeFuture.all(appIdents.stream()
                                .map(appIdent -> {
                                    var ak = appIdent.getString("ak");
                                    var sk = appIdent.getString("sk");
                                    var appId = appIdent.getLong("rel_app_id");
                                    var appCode = appIdent.getString("open_id");
                                    var validTime = appIdent.getLong("valid_time");
                                    var tenantId = appIdent.getLong("rel_tenant_id");
                                    return changeAppIdent(ak, sk, validTime, appId, appCode, tenantId, context);
                                })
                                .collect(Collectors.toList())))
                .compose(resp -> context.helper.success());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthPolicyInfo implements Serializable {

        private String resourceUri;

        private OptActionKind actionKind;

        private AuthSubjectKind subjectKind;

        private String subjectId;

        private AuthSubjectOperatorKind subjectOperator;

    }

}
