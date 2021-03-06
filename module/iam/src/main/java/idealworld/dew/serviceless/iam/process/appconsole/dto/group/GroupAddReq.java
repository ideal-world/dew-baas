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

package idealworld.dew.serviceless.iam.process.appconsole.dto.group;

import idealworld.dew.framework.DewConstant;
import idealworld.dew.serviceless.iam.dto.ExposeKind;
import idealworld.dew.serviceless.iam.dto.GroupKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 添加角色定义请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupAddReq implements Serializable {

    // 群组编码
    @NotNull
    @NotBlank
    @Size(max = 255)
    private String code;
    // 群组类型
    @NotNull
    private GroupKind kind;
    // 群组名称
    @NotNull
    @NotBlank
    @Size(max = 255)
    private String name;
    // 群组图标（路径）
    @Size(max = 1000)
    @Builder.Default
    private String icon = "";
    // 显示排序，asc
    @Builder.Default
    private Integer sort = 0;
    // 关联群组Id，用于多树合成
    @Builder.Default
    private Long relGroupId = DewConstant.OBJECT_UNDEFINED;
    // 关联群起始组节点Id，用于多树合成
    @Builder.Default
    private Long relGroupNodeId = DewConstant.OBJECT_UNDEFINED;
    // 开放等级类型
    @Builder.Default
    private ExposeKind exposeKind = ExposeKind.APP;

}
