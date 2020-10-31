package idealworld.dew.baas.gateway.util;

import idealworld.dew.baas.common.util.CacheHelper;
import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * @author gudaoxuri
 */
@Slf4j
public class FutureCacheHelper extends CacheHelper {

    public static <E> Future<E> getSetF(String key, Integer cacheSec, Supplier<Future<E>> elseFun) {
        var valueInfo = CACHES.getOrDefault(key, null);
        return Future.future(promise -> {
            if (valueInfo == null
                    || valueInfo.get() == null
                    || ((long) valueInfo.get()[0]) < System.currentTimeMillis()) {
                elseFun.get()
                        .onSuccess(value -> {
                            set(key, value, cacheSec);
                            promise.complete(value);
                        })
                        .onFailure(e -> {
                            log.error("Cache getSet error {}", e.getMessage(), e.getCause());
                        });
            } else {
                log.trace("Cache hit : {}", key);
                promise.complete((E)valueInfo.get()[1]);
            }
        });

    }

}
