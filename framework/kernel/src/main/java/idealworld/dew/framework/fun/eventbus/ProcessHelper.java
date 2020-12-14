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

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Page;
import idealworld.dew.framework.domain.IdEntity;
import idealworld.dew.framework.dto.IdResp;
import idealworld.dew.framework.exception.BadRequestException;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gudaoxuri
 */
public class ProcessHelper {

    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    public <E> Future<E> success(E result) {
        return Future.succeededFuture(result);
    }

    public <E> Future<E> error(Throwable e) {
        return Future.failedFuture(e);
    }

    public <E> E convert(Object bean, Class<E> clazz) {
        return $.bean.copyProperties(bean, clazz);
    }

    public <E extends IdResp, I extends IdEntity> Future<E> success(I entity, Class<E> clazz) {
        return Future.succeededFuture($.bean.copyProperties(entity, clazz));
    }

    public <E extends IdResp, I extends IdEntity> Future<List<E>> success(List<I> entities, Class<E> clazz) {
        return Future.succeededFuture(
                entities.stream()
                        .map(r -> $.bean.copyProperties(r, clazz))
                        .collect(Collectors.toList()));
    }

    public <E extends IdResp, I extends IdEntity> Future<Page<E>> success(Page<I> entities, Class<E> clazz) {
        var page = new Page<E>();
        page.setPageNumber(entities.getPageNumber());
        page.setPageSize(entities.getPageSize());
        page.setPageTotal(entities.getPageTotal());
        page.setRecordTotal(entities.getRecordTotal());
        page.setObjects(entities.getObjects().stream()
                .map(r -> $.bean.copyProperties(r, clazz))
                .collect(Collectors.toList()));
        return Future.succeededFuture(page);
    }

    public <E> E parseBody(Buffer body, Class<E> bodyClazz, String... excludeKeys) {
        if (bodyClazz == Void.class) {
            return null;
        }
        var jsonBody = new JsonObject(body.toString(StandardCharsets.UTF_8));
        trimValues(jsonBody, Arrays.asList(excludeKeys));
        var beanBody = jsonBody.mapTo(bodyClazz);
        var violations = VALIDATOR.validate(beanBody);
        if (violations.isEmpty()) {
            return beanBody;
        }
        var errorMsg = violations.stream()
                .map(violation -> "[" + violation.getPropertyPath().toString() + "]" + violation.getMessage())
                .collect(Collectors.joining("\n"));
        throw new BadRequestException(errorMsg);
    }

    private void trimValues(JsonObject json, List<String> excludeKeys) {
        json.stream()
                .filter(j -> !excludeKeys.contains(j.getKey()))
                .forEach(j -> {
                    if (j.getValue() instanceof JsonObject) {
                        trimValues((JsonObject) j.getValue(), excludeKeys.stream().map(k -> k.substring(j.getKey().length() + 1)).collect(Collectors.toList()));
                    } else if (j.getValue() instanceof JsonArray) {
                        ((JsonArray) j.getValue()).forEach(i -> {
                            if (j instanceof JsonObject) {
                                trimValues((JsonObject) i, excludeKeys.stream().map(k -> k.substring(j.getKey().length() + 1)).collect(Collectors.toList()));
                            }
                        });
                    } else if (j.getValue() instanceof String) {
                        json.put(j.getKey(), ((String) j.getValue()).trim());
                    }
                });
    }

}
