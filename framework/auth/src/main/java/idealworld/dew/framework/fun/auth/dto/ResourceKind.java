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

package idealworld.dew.framework.fun.auth.dto;

import idealworld.dew.framework.exception.BadRequestException;

import java.util.Arrays;

/**
 * 资源类型枚举.
 *
 * @author gudaoxuri
 */
public enum ResourceKind {

    /**
     * 菜单.
     */
    MENU("MENU"),
    /**
     * 页面元素.
     */
    ELEMENT("ELEMENT"),
    /**
     * OAuth.
     */
    OAUTH("OAUTH"),
    /**
     * 关系数据库.
     */
    RELDB("RELDB"),
    /**
     * 缓存.
     */
    CACHE("CACHE"),
    /**
     * MQ.
     */
    MQ("MQ"),
    /**
     * 对象存储.
     */
    OBJECT("OBJECT"),
    /**
     * Task.
     */
    TASK("TASK"),
    /**
     * HTTP(s).
     */
    HTTP("HTTP");

    private final String code;

    ResourceKind(String code) {
        this.code = code;
    }

    /**
     * Parse resource kind.
     *
     * @param code the code
     * @return the resource kind
     */
    public static ResourceKind parse(String code) {
        return Arrays.stream(ResourceKind.values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Resource kind {" + code + "} NOT exist."));
    }

    @Override
    public String toString() {
        return code;
    }
}
