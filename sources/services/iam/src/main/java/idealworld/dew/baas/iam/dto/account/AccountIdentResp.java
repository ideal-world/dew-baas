/*
 * Copyright 2020. the original author or authors.
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

package idealworld.dew.baas.iam.dto.account;

import idealworld.dew.baas.common.dto.IdResp;
import idealworld.dew.baas.iam.enumeration.AccountIdentKind;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 账号认证响应.
 *
 * @author gudaoxuri
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(title = "账号认证响应")
public class AccountIdentResp extends IdResp {

    @NotNull
    @Schema(title = "账号认证类型", required = true)
    private AccountIdentKind kind;

    @NotNull
    @NotBlank
    @Size(max = 255)
    @Schema(title = "账号认证名称", required = true)
    private String ak;

    @NotNull
    @Schema(title = "账号认证有效开始时间", required = true)
    private Date validStartTime;

    @NotNull
    @Schema(title = "账号认证有效结束时间", required = true)
    private Date validEndTime;

    @NotNull
    @Schema(title = "关联账号Id", required = true)
    private Long relAccountId;

}
