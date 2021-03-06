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

package idealworld.dew.framework.fun.httpserver;

import lombok.extern.slf4j.Slf4j;

/**
 * HTTP处理器.
 *
 * @author gudaoxuri
 */
@Slf4j
public abstract class AuthHttpHandler extends KernelHttpHandler {

    protected static final String CONTEXT_INFO = "CONTEXT";

    public AuthHttpHandler(String moduleName) {
        super(moduleName);
    }

}
