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

package idealworld.dew.serviceless.iam.process.appconsole.dto.resource;

import idealworld.dew.serviceless.iam.process.common.dto.AppBasedResp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 资源响应.
 *
 * @author gudaoxuri
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResourceResp extends AppBasedResp {

    // 资源名称
    @NotNull
    @NotBlank
    @Size(max = 255)
    private String name;
    // 资源路径
    @NotNull
    @NotBlank
    @Size(max = 5000)
    private String pathAndQuery;
    // 资源图标（路径）
    @Size(max = 1000)
    private String icon;
    // 触发后的操作，多用于菜单链接
    @Size(max = 1000)
    private String action;
    @NotNull
    // 资源显示排序，asc
    private Integer sort;
    @NotNull
    // 是否是资源组
    private Boolean resGroup;
    @NotNull
    // 资源所属组Id
    private Long parentId;
    // 关联资源主体Id
    @NotNull
    private Long relResourceSubjectId;

}
