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

package idealworld.dew.serviceless.iam.scene.appconsole.dto.authpolicy;

import idealworld.dew.serviceless.common.enumeration.AuthResultKind;
import idealworld.dew.serviceless.common.enumeration.AuthSubjectKind;
import idealworld.dew.serviceless.common.enumeration.AuthSubjectOperatorKind;
import idealworld.dew.serviceless.common.enumeration.OptActionKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * 修改权限策略请求.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 修改权限策略请求")
public class AuthPolicyModifyReq implements Serializable {

    // 关联权限主体类型")
    private AuthSubjectKind relSubjectKind;

    @Size(max = 10000)
    // 关联权限主体Ids,有多个时逗号分隔")
    private String relSubjectIds;

    // 关联权限主体运算类型")
    private AuthSubjectOperatorKind subjectOperator;

    // 生效时间")
    protected Date effectiveTime;

    // 失效时间")
    protected Date expiredTime;

    // 关联资源Id")
    private Long relResourceId;

    // 操作类型")
    private OptActionKind actionKind;

    // 操作结果")
    private AuthResultKind resultKind;

    // 是否排他")
    private Boolean exclusive;

}
