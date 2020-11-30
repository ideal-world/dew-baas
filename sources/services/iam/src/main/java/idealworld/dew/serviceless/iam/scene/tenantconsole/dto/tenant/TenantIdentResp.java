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

package idealworld.dew.serviceless.iam.scene.tenantconsole.dto.tenant;

import idealworld.dew.serviceless.common.dto.IdResp;
import idealworld.dew.serviceless.iam.enumeration.AccountIdentKind;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 租户认证配置响应.
 *
 * @author gudaoxuri
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(title = "租户认证配置响应")
public class TenantIdentResp extends IdResp {

    @NotNull
    @Schema(title = "租户认证类型名称",required = true)
    private AccountIdentKind kind;

    @NotNull
    @NotBlank
    @Size(max = 2000)
    @Schema(title = "认证AK校验正则规则说明",required = true)
    private String validAKRuleNote;

    @NotNull
    @NotBlank
    @Size(max = 2000)
    @Schema(title = "认证AK校验正则规则",required = true)
    private String validAKRule;

    @NotNull
    @NotBlank
    @Size(max = 2000)
    @Schema(title = "认证SK校验正则规则说明",required = true)
    private String validSKRuleNote;

    @NotNull
    @NotBlank
    @Size(max = 2000)
    @Schema(title = "认证SK校验正则规则",required = true)
    private String validSKRule;

    @NotNull
    @Schema(title = "认证有效时间（秒）",required = true)
    private Long validTimeSec;

}
