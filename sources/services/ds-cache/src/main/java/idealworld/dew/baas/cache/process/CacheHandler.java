package idealworld.dew.baas.cache.process;

import com.ecfront.dew.common.StandardCode;
import idealworld.dew.baas.cache.CacheConstant;
import idealworld.dew.baas.common.Constant;
import idealworld.dew.baas.common.enumeration.OptActionKind;
import idealworld.dew.baas.common.funs.cache.RedisClient;
import idealworld.dew.baas.common.funs.httpserver.CommonHttpHandler;
import idealworld.dew.baas.common.util.URIHelper;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 缓存处理器
 *
 * @author gudaoxuri
 */
@Slf4j
public class CacheHandler extends CommonHttpHandler {

    @Override
    public void handle(RoutingContext ctx) {
        if (!ctx.request().headers().contains(Constant.REQUEST_RESOURCE_URI_FLAG)
                || !ctx.request().headers().contains(Constant.REQUEST_RESOURCE_ACTION_FLAG)) {
            error(StandardCode.BAD_REQUEST, "请求格式不合法", ctx);
            return;
        }
        var strResourceUriWithoutPath = ctx.request().getHeader(Constant.REQUEST_RESOURCE_URI_FLAG);
        var resourceUri = URIHelper.newURI(strResourceUriWithoutPath);
        var resourceSubjectCode = resourceUri.getHost();
        var resourcePath = resourceUri.getPath().substring(1).split("/");
        var resourceQuery = URIHelper.getSingleValueQuery(resourceUri.getQuery());
        if (!RedisClient.contains(resourceSubjectCode)) {
            error(StandardCode.BAD_REQUEST, "请求的资源主题不存在", ctx);
            return;
        }
        if (resourcePath.length < 1) {
            error(StandardCode.BAD_REQUEST, "请求的格式不正确", ctx);
            return;
        }
        var key = resourcePath[0];
        var fieldKey = resourcePath.length == 2 ? resourcePath[1] : null;
        var action = OptActionKind.parse(ctx.request().getHeader(Constant.REQUEST_RESOURCE_ACTION_FLAG));
        var redisClient = RedisClient.choose(resourceSubjectCode);
        switch (action) {
            case EXISTS:
                if (fieldKey == null) {
                    redisClient.exists(key).onComplete(handler -> resultProcess(handler, ctx));
                } else {
                    redisClient.hexists(key, fieldKey).onComplete(handler -> resultProcess(handler, ctx));
                }
                break;
            case FETCH:
                if (fieldKey == null) {
                    redisClient.get(key).onComplete(handler -> resultProcess(handler, ctx));
                } else if (fieldKey.isBlank() || fieldKey.equals("*")) {
                    redisClient.hgetall(key).onComplete(handler -> resultProcess(handler, ctx));
                } else {
                    redisClient.hget(key, fieldKey).onComplete(handler -> resultProcess(handler, ctx));
                }
                break;
            case CREATE:
            case MODIFY:
                var body = ctx.getBodyAsString();
                var attrExpire = resourceQuery.containsKey(CacheConstant.REQUEST_ATTR_EXPIRE) ? Long.parseLong(resourceQuery.get(CacheConstant.REQUEST_ATTR_EXPIRE)) : null;
                var attrIncr = resourceQuery.containsKey(CacheConstant.REQUEST_ATTR_INCR);

                if (fieldKey == null) {
                    if (body != null && !body.isBlank() && attrIncr) {
                        redisClient.incrby(key, Integer.valueOf(body)).onComplete(handler -> resultProcess(handler, ctx));
                        break;
                    }
                    if (body != null && !body.isBlank() && attrExpire == null) {
                        redisClient.set(key, body).onComplete(handler -> resultProcess(handler, ctx));
                        break;
                    }
                    if (body != null && !body.isBlank() && attrExpire != null) {
                        redisClient.setex(key, body, attrExpire).onComplete(handler -> resultProcess(handler, ctx));
                        break;
                    }
                    if ((body == null || body.isBlank()) && attrExpire != null) {
                        redisClient.expire(key, attrExpire).onComplete(handler -> resultProcess(handler, ctx));
                        break;
                    }
                }
                if (fieldKey != null) {
                    if (body != null && !body.isBlank() && attrIncr) {
                        redisClient.hincrby(key, fieldKey, Integer.valueOf(body)).onComplete(handler -> resultProcess(handler, ctx));
                        break;
                    }
                    if (body != null && !body.isBlank() && attrExpire == null) {
                        redisClient.hset(key, fieldKey, body).onComplete(handler -> resultProcess(handler, ctx));
                        break;
                    }
                    if (attrExpire != null) {
                        redisClient.expire(key, attrExpire).onComplete(handler -> resultProcess(handler, ctx));
                        break;
                    }
                }
                log.warn("[Cache]Unsupported operations: action = {}, key = {}, fieldKey = {}, body = {}, expire = {}", action, key, fieldKey, body, attrExpire);
                error(StandardCode.BAD_REQUEST, "请求的格式不正确", ctx);
                break;
            case DELETE:
                if (fieldKey == null || fieldKey.isBlank() || fieldKey.equals("*")) {
                    redisClient.del(key).onComplete(handler -> resultProcess(handler, ctx));
                } else {
                    redisClient.hdel(key, fieldKey).onComplete(handler -> resultProcess(handler, ctx));
                }
                break;
        }

    }

    private <E> void resultProcess(AsyncResult<E> handler, RoutingContext ctx) {
        if (handler.failed()) {
            error(StandardCode.INTERNAL_SERVER_ERROR, "服务错误", ctx, handler.cause());
            return;
        }
        var result = handler.result();
        if (result == null) {
            ctx.end("");
        } else if (result instanceof String) {
            ctx.end((String) result);
        } else if (result instanceof Number) {
            ctx.end(String.valueOf(result));
        } else if (result instanceof Boolean) {
            ctx.end(String.valueOf(result));
        } else {
            ctx.end(JsonObject.mapFrom(result).toBuffer());
        }
    }


}