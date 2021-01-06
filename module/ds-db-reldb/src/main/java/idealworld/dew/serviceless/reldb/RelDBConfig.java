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

package idealworld.dew.serviceless.reldb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * RelDB config.
 *
 * @author gudaoxuri
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelDBConfig {

    @Builder.Default
    private Security security = new Security();
    @Builder.Default
    private ContextFlag contextFlag = new ContextFlag();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Security {

        @Builder.Default
        private Integer appInfoCacheExpireSec = 60;
        @Builder.Default
        private Integer resourceCacheExpireSec = 60 * 60 * 24;
        @Builder.Default
        private Integer groupNodeLength = 5;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContextFlag {

        @Builder.Default
        private String currentUserId = "#{USER_ID}";
        @Builder.Default
        private String currentTimestamp = "#{TIMESTAMP}";

    }

}
