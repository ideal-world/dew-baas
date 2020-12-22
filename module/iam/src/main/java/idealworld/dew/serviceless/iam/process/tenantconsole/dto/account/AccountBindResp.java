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

package idealworld.dew.serviceless.iam.process.tenantconsole.dto.account;

import idealworld.dew.framework.dto.IdResp;
import idealworld.dew.serviceless.iam.dto.AccountIdentKind;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

/**
 * 账号绑定响应.
 *
 * @author gudaoxuri
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountBindResp extends IdResp {

    // 源租户Id
    @NotNull
    private Long fromTenantId;
    // 源租户账号Id
    @NotNull
    private Long fromAccountId;
    // 目标租户Id
    @NotNull
    private Long toTenantId;
    // 目标户账号Id
    @NotNull
    private Long toAccountId;
    // 绑定使用的账号认证类型名称
    @NotNull
    private AccountIdentKind identKind;

}