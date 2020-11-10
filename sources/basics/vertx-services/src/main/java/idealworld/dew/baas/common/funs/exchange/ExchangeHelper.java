package idealworld.dew.baas.common.funs.exchange;

import com.ecfront.dew.common.$;
import idealworld.dew.baas.common.Constant;
import idealworld.dew.baas.common.dto.ExchangeData;
import idealworld.dew.baas.common.funs.cache.RedisClient;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.function.Consumer;

/**
 * @author gudaoxuri
 */
@Slf4j
public class ExchangeHelper {

    private static final String QUICK_CHECK_SPLIT = "#";

    public static Future<Void> register(Set<String> subjectCategories, Consumer<ExchangeData> fun) {
        Promise<Void> promise = Promise.promise();
        RedisClient.choose("").subscribe(Constant.CONFIG_EVENT_NOTIFY_TOPIC_BY_IAM, message -> {
            var quickCheckSplit = message.split(QUICK_CHECK_SPLIT);
            var subjectCategory = quickCheckSplit[0];
            if (!subjectCategories.contains(subjectCategory)) {
                return;
            }
            log.trace("[Exchange]Received {}", message);
            fun.accept($.json.toObject(quickCheckSplit[1], ExchangeData.class));
        }).onSuccess(response -> promise.complete());
        return promise.future();
    }

}
