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

package idealworld.dew.serviceless.iam.scene.appconsole.dto.group;

import idealworld.dew.serviceless.common.Constant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 修改群组节点请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "修改群组节点请求")
public class GroupNodeModifyReq implements Serializable {

    @Size(max = 1000)
    @Schema(title = "业务编码")
    private String busCode;

    @Size(max = 255)
    @Schema(title = "节点名称")
    private String name;

    @Size(max = 2000)
    @Schema(title = "节点扩展信息，Json格式")
    private String parameters;

    @Schema(title = "上级节点Id")
    @Builder.Default
    private Long parentId = Constant.OBJECT_UNDEFINED;

    @Schema(title = "同级上一个节点Id")
    @Builder.Default
    private Long siblingId = Constant.OBJECT_UNDEFINED;

}
