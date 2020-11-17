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

package idealworld.dew.baas.iam.test.scene;

import idealworld.dew.baas.iam.scene.systemconsole.dto.TenantAddReq;
import idealworld.dew.baas.iam.test.BasicTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SystemConsoleTest extends BasicTest {

    @Test
    public void testAddTenant() {
        loginBySystemAdmin();
        // 租户注册
        var tenantId = postToEntity("/console/system/tenant", TenantAddReq.builder()
                .name("xyy")
                .build(), Long.class).getBody();
        Assertions.assertNotNull(tenantId);
    }
}
