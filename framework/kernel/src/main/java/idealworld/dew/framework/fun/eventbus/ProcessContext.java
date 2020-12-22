/*
 * Copyright 2020. gudaoxuri
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

package idealworld.dew.framework.fun.eventbus;

import idealworld.dew.framework.domain.IdEntity;
import idealworld.dew.framework.domain.SafeEntity;
import idealworld.dew.framework.dto.IdentOptInfo;
import idealworld.dew.framework.fun.cache.FunRedisClient;
import idealworld.dew.framework.fun.httpclient.FunHttpClient;
import idealworld.dew.framework.fun.sql.FunSQLClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author gudaoxuri
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessContext {

    private IdentOptInfo identOptInfo;

    public ProcessContext init(IdentOptInfo identOptInfo) {
        this.identOptInfo = identOptInfo;
        if (FunSQLClient.contains(moduleName)) {
            sql = FunSQLClient.choose(moduleName);
            if (sql.addEntityByInsertFun == null) {
                sql.addEntityByInsertFun = o -> addSafeInfo((IdEntity) o, true);
            }
            if (sql.addEntityByUpdateFun == null) {
                sql.addEntityByUpdateFun = o -> addSafeInfo((IdEntity) o, false);
            }
        }
        if (FunRedisClient.contains(moduleName)) {
            cache = FunRedisClient.choose(moduleName);
        }
        if (FunHttpClient.contains(moduleName)) {
            http = FunHttpClient.choose(moduleName);
        }
        if (FunEventBus.contains(moduleName)) {
            eb = FunEventBus.choose(moduleName);
        }
        return this;
    }

    @Builder.Default
    public ProcessHelper helper = new ProcessHelper();
    public Object conf;
    public String moduleName;
    public FunSQLClient sql;
    public FunRedisClient cache;
    public FunHttpClient http;
    public FunEventBus eb;

    private <E extends IdEntity> void addSafeInfo(E entity, Boolean insert) {
        if (entity instanceof SafeEntity) {
            if (insert) {
                ((SafeEntity) entity).setCreateUser(
                        identOptInfo.getAccountCode() != null
                                ? (String) identOptInfo.getAccountCode() :
                                "");
            }
            ((SafeEntity) entity).setUpdateUser(
                    identOptInfo.getAccountCode() != null
                            ? (String) identOptInfo.getAccountCode() :
                            "");
        }
    }

}