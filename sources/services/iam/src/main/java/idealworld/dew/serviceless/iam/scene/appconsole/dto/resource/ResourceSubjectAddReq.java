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

package idealworld.dew.serviceless.iam.scene.appconsole.dto.resource;

import idealworld.dew.serviceless.common.enumeration.ResourceKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 添加资源主体请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 添加资源主体请求")
public class ResourceSubjectAddReq implements Serializable {

    @NotNull
    @NotBlank
    @Size(max = 255)
    // 资源主体编码后缀
    private String codePostfix;

    @NotNull
    @NotBlank
    @Size(max = 255)
    // 资源主体名称
    private String name;

    // 资源主体显示排序，asc")
    @Builder.Default
    private Integer sort = 0;

    @NotNull
    // 资源类型
    private ResourceKind kind;

    @NotNull
    @NotBlank
    @Size(max = 5000)
    // 资源主体连接URI
    private String uri;

    @Size(max = 1000)
    // AK，部分类型支持写到URI中")
    @Builder.Default
    private String ak = "";

    @Size(max = 1000)
    // SK，部分类型支持写到URI中")
    @Builder.Default
    private String sk = "";

    @Size(max = 1000)
    // 第三方平台账号名")
    @Builder.Default
    private String platformAccount = "";

    @Size(max = 1000)
    // 第三方平台项目名，如华为云的ProjectId")
    @Builder.Default
    private String platformProjectId = "";

    // 执行超时")
    @Builder.Default
    private Long timeoutMS = 0L;

}
