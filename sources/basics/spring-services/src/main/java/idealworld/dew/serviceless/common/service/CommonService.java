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

package idealworld.dew.serviceless.common.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * The type Common service.
 *
 * @param <P> the type parameter
 * @author gudaoxuri
 */
@Slf4j
public abstract class CommonService<P extends Serializable> implements StorageService<P> {

    /**
     * The Sql builder.
     */
    @Autowired
    protected JPAQueryFactory sqlBuilder;
    /**
     * The Entity manager.
     */
    @Autowired
    protected EntityManager entityManager;

    @Override
    final public JPAQueryFactory sqlBuilder() {
        return sqlBuilder;
    }

    @Override
    final public EntityManager entityManager() {
        return entityManager;
    }

    @Override
    public Logger log() {
        return log;
    }

}
