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

package idealworld.dew.serviceless.iam.domain.ident;

import idealworld.dew.serviceless.common.domain.SafeEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;

/**
 * 应用认证.
 *
 * @author gudaoxuri
 */
@Entity
@Table(name = "iam_app_ident", indexes = {
        @Index(columnList = "ak", unique = true),
        @Index(columnList = "relAppId,validTime")
})
@org.hibernate.annotations.Table(appliesTo = "iam_app_ident",
        comment = "应用认证")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AppIdent extends SafeEntity {

    @Column(nullable = false,
            columnDefinition = "varchar(1000) comment '应用认证用途'")
    private String note;

    @Column(nullable = false,
            columnDefinition = "varchar(255) comment '应用认证名称（Access Key Id）'")
    private String ak;

    @Column(nullable = false,
            columnDefinition = "varchar(1000) comment '应用认证密钥（Secret Access Key）'")
    private String sk;

    @Column(nullable = false,
            columnDefinition = "datetime(6) comment '应用认证有效时间'")
    private Date validTime;

    @Column(nullable = false,
            columnDefinition = "bigint comment '关联应用Id'")
    private Long relAppId;

}
