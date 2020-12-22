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

package idealworld.dew.serviceless.http.exchange;

import idealworld.dew.framework.DewConfig;
import idealworld.dew.framework.fun.auth.dto.ResourceKind;
import idealworld.dew.framework.fun.auth.exchange.ExchangeHelper;
import idealworld.dew.framework.fun.httpclient.FunHttpClient;
import idealworld.dew.framework.fun.sql.FunSQLClient;
import idealworld.dew.serviceless.http.HttpConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gudaoxuri
 */
@Slf4j
public class HttpExchangeProcessor {

    public static Future<Void> init(String moduleName, HttpConfig httpConfig, Vertx vertx) {
        return ExchangeHelper.loadAndWatchResourceSubject(moduleName, ResourceKind.HTTP,
                resourceSubjectExchange ->
                        FunHttpClient.init(resourceSubjectExchange.getCode(), vertx,
                                DewConfig.FunConfig.HttpClientConfig.builder()
                                        .uri(resourceSubjectExchange.getUri())
                                        .timeoutMs(resourceSubjectExchange.getTimeoutMs())
                                        .build()),
                FunSQLClient::remove);
    }

}