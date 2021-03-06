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

package idealworld.dew.serviceless.iam.dto;

import idealworld.dew.framework.exception.BadRequestException;

import java.util.Arrays;

/**
 * 账号认证类型枚举.
 * <p>
 * 编码属性URI的Host部分，不能带下划线
 *
 * @author gudaoxuri
 */
public enum AccountIdentKind {

    /**
     * 用户名 + 密码.
     */
    USERNAME("USERNAME"),
    /**
     * 租户间授权认证.
     */
    AUTH_IDENT("AUTH-IDENT"),
    /**
     * 手机号 + 验证码.
     */
    PHONE("PHONE"),
    /**
     * 邮箱 + 密码.
     */
    EMAIL("EMAIL"),
    /**
     * 微信小程序OAuth.
     */
    WECHAT_XCX("WECHAT-XCX");

    private final String code;

    AccountIdentKind(String code) {
        this.code = code;
    }

    /**
     * Parse account ident kind.
     *
     * @param code the code
     * @return the account ident kind
     */
    public static AccountIdentKind parse(String code) {
        return Arrays.stream(AccountIdentKind.values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Account Ident kind {" + code + "} NOT exist."));
    }

    @Override
    public String toString() {
        return code;
    }
}
