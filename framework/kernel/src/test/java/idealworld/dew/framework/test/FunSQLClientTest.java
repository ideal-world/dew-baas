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

package idealworld.dew.framework.test;

import idealworld.dew.framework.DewConfig;
import idealworld.dew.framework.domain.IdEntity;
import idealworld.dew.framework.domain.SoftDelEntity;
import idealworld.dew.framework.dto.CommonStatus;
import idealworld.dew.framework.dto.IdentOptCacheInfo;
import idealworld.dew.framework.fun.eventbus.ProcessContext;
import idealworld.dew.framework.fun.sql.FunSQLClient;
import idealworld.dew.framework.fun.test.DewTest;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FunSQLClientTest extends DewTest {

    static {
        enableMysql();
    }

    private static FunSQLClient funSQLClient;

    @BeforeAll
    public static void before(Vertx vertx, VertxTestContext testContext) {
        FunSQLClient.init("", vertx, DewConfig.FunConfig.SQLConfig.builder()
                .host("127.0.0.1")
                .port(mysqlConfig.getFirstMappedPort())
                .db(mysqlConfig.getDatabaseName())
                .userName(mysqlConfig.getUsername())
                .password(mysqlConfig.getPassword())
                .build());
        funSQLClient = FunSQLClient.choose("");
        testContext.completeNow();
    }

    @Test
    @Order(1)
    public void testDDL(Vertx vertx, VertxTestContext testContext) {
        Future.succeededFuture()
                .compose(resp ->
                        funSQLClient.ddl("drop table if exists account")
                )
                .compose(resp ->
                        funSQLClient.ddl("create table if not exists account\n" +
                                "(\n" +
                                "\tid bigint auto_increment primary key,\n" +
                                "\tname varchar(255) not null unique comment '账号名称',\n" +
                                "\topen_id varchar(100) not null comment 'Open Id',\n" +
                                "\tstatus varchar(50) not null comment '账号状态'\n" +
                                ")\n" +
                                "comment '账号'")
                )
                .onSuccess(resp -> {
                    testContext.completeNow();
                });
    }

    @Test
    @Order(2)
    public void testSimple(Vertx vertx, VertxTestContext testContext) {
        Future.succeededFuture()
                .compose(resp ->
                        funSQLClient.update("insert into account(name, open_id, status) values (#{name}, #{open_id}, #{status})",
                                new HashMap<>() {
                                    {
                                        put("name", "孤岛旭日1");
                                        put("open_id", "xxxx");
                                        put("status", "ENABLED");
                                    }
                                },
                                null)
                )
                .compose(id ->
                        funSQLClient.update("update account set name = #{name} where id = #{id}",
                                new HashMap<>() {
                                    {
                                        put("name", "孤岛旭日_new");
                                        put("id", id);
                                    }
                                },
                                null)
                )
                .compose(resp ->
                        funSQLClient.update("insert into account(name, open_id, status) values (#{name}, #{open_id}, #{status})",
                                new ArrayList<>() {
                                    {
                                        add(new HashMap<>() {
                                            {
                                                put("name", "孤岛旭日2");
                                                put("open_id", "xxxx");
                                                put("status", "ENABLED");
                                            }
                                        });
                                        add(new HashMap<>() {
                                            {
                                                put("name", "孤岛旭日3");
                                                put("open_id", "xxxx");
                                                put("status", "ENABLED");
                                            }
                                        });
                                    }
                                },
                                null)
                )
                .compose(resp ->
                        funSQLClient.list("select * from account", new HashMap<>(), null)
                )
                .compose(resp -> {
                    Assertions.assertEquals(3, resp.size());
                    Assertions.assertEquals("孤岛旭日_new", resp.get(0).getString("name"));
                    Assertions.assertEquals("ENABLED", resp.get(0).getString("status"));
                    return Future.succeededFuture();
                })
                .compose(resp ->
                        funSQLClient.getOne("select * from account limit 1", new HashMap<>(), null)
                )
                .compose(resp -> {
                    return Future.succeededFuture();
                })
                .compose(resp ->
                        funSQLClient.count("select * from account", new HashMap<>(), null)
                )
                .compose(resp -> {
                    Assertions.assertEquals(3, resp);
                    return Future.succeededFuture();
                })
                .compose(resp ->
                        funSQLClient.exists("select * from account", new HashMap<>(), null)
                )
                .compose(resp -> {
                    return Future.succeededFuture();
                })
                .compose(resp ->
                        funSQLClient.page("select * from %s", new HashMap<>(), ProcessContext.builder()
                                .req(ProcessContext.Request.builder()
                                        .params(new HashMap<>() {
                                            {
                                                put("pageNumber", "2");
                                                put("pageSize", "2");
                                            }
                                        }).build()).build(), "account")
                )
                .onSuccess(resp -> {
                    Assertions.assertEquals(3, resp.getRecordTotal());
                    Assertions.assertEquals(2, resp.getPageTotal());
                    Assertions.assertEquals(2, resp.getPageNumber());
                    Assertions.assertEquals(2, resp.getPageSize());
                    Assertions.assertEquals("孤岛旭日3", resp.getObjects().get(0).getString("name"));
                    Assertions.assertEquals("ENABLED", resp.getObjects().get(0).getString("status"));
                    testContext.completeNow();
                })
                .onFailure(testContext::failNow);
    }

    @Test
    @Order(3)
    public void testEntity(Vertx vertx, VertxTestContext testContext) {
        Future.succeededFuture()
                .compose(resp ->
                        funSQLClient.save(Account.builder()
                                        .name("孤岛旭日4")
                                        .openId("yyyy")
                                        .status(CommonStatus.ENABLED)
                                        .build(),
                                null
                        )
                )
                .compose(resp ->
                        funSQLClient.save(new ArrayList<>() {
                                              {
                                                  add(Account.builder()
                                                          .name("孤岛旭日5")
                                                          .openId("yyyy")
                                                          .status(CommonStatus.ENABLED)
                                                          .build());
                                                  add(Account.builder()
                                                          .name("孤岛旭日6")
                                                          .openId("yyyy")
                                                          .status(CommonStatus.ENABLED)
                                                          .build());
                                              }
                                          },
                                null)
                )
                .compose(resp ->
                        funSQLClient.list("select * from account where name in (#{n1}, #{n2}, #{n3})", new HashMap<>() {
                            {
                                put("n1", "孤岛旭日4");
                                put("n2", "孤岛旭日5");
                                put("n3", "孤岛旭日6");
                            }
                        }, Account.class, null)
                )
                .compose(resp -> {
                    Assertions.assertEquals(3, resp.size());
                    Assertions.assertEquals("孤岛旭日4", resp.get(0).name);
                    Assertions.assertEquals("yyyy", resp.get(0).openId);
                    Assertions.assertEquals(CommonStatus.ENABLED, resp.get(0).status);
                    return Future.succeededFuture();
                })
                .compose(resp ->
                        funSQLClient.list(new HashMap<>() {
                            {
                                put("-name", "孤岛旭日4");
                            }
                        }, Account.class, null)
                )
                .compose(resp -> {
                    Assertions.assertFalse(resp.stream().anyMatch(r -> r.name.equalsIgnoreCase("孤岛旭日4")));
                    return Future.succeededFuture();
                })
                .compose(resp ->
                        funSQLClient.getOne(new HashMap<>() {
                            {
                                put("name", "孤岛旭日4");
                            }
                        }, Account.class, null)
                )
                .compose(resp -> {
                    Assertions.assertEquals("孤岛旭日4", resp.name);
                    Assertions.assertEquals("yyyy", resp.openId);
                    Assertions.assertEquals(CommonStatus.ENABLED, resp.status);
                    return Future.succeededFuture(resp.getId());
                })
                .compose(id ->
                        funSQLClient.update(id, Account.builder().name("孤岛旭日4_new").build(), null)
                )
                .compose(resp ->
                        funSQLClient.list("select * from account where name in (#{n1})", new HashMap<>() {
                            {
                                put("n1", "孤岛旭日4_new");
                            }
                        }, Account.class, null)
                )
                .compose(resp ->
                        funSQLClient.getOne(resp.get(0).getId(), Account.class, null)
                )
                .compose(resp -> {
                    Assertions.assertEquals("孤岛旭日4_new", resp.name);
                    Assertions.assertEquals("yyyy", resp.openId);
                    Assertions.assertEquals(CommonStatus.ENABLED, resp.status);
                    return Future.succeededFuture();
                })
                .compose(resp ->
                        funSQLClient.softDelete("select * from account where name in (#{n1})", new HashMap<>() {
                            {
                                put("n1", "孤岛旭日6");
                            }
                        }, Account.class, ProcessContext.builder()
                                .req(ProcessContext.Request.builder()
                                        .identOptInfo(new IdentOptCacheInfo()).build()).build())
                )
                .compose(resp ->
                        funSQLClient.delete("delete from account where name = #{n1}", new HashMap<>() {
                                    {
                                        put("n1", "孤岛旭日5");
                                    }
                                },
                                null)
                )
                .compose(resp ->
                        funSQLClient.list("select * from account", new HashMap<>(), Account.class, null)
                )
                .compose(resp -> {
                    Assertions.assertEquals(4, resp.size());
                    Assertions.assertEquals("孤岛旭日4_new", resp.get(3).name);
                    Assertions.assertEquals("yyyy", resp.get(3).openId);
                    Assertions.assertEquals(CommonStatus.ENABLED, resp.get(3).status);
                    return Future.succeededFuture();
                })
                .compose(resp ->
                        funSQLClient.list("select * from " + new SoftDelEntity().tableName(), new HashMap<>(), SoftDelEntity.class, null)
                )
                .onSuccess(resp -> {
                    Assertions.assertEquals(1, resp.size());
                    Assertions.assertTrue(resp.get(0).getContent().contains("孤岛旭日6"));
                    testContext.completeNow();
                })
                .onFailure(testContext::failNow);
    }

    @Test
    @Order(4)
    public void testTXSuccess(Vertx vertx, VertxTestContext testContext) {
        funSQLClient.tx(client ->
                Future.succeededFuture()
                        .compose(resp ->
                                funSQLClient.save(
                                        Account.builder()
                                                .name("idealworld1")
                                                .openId("yyyy")
                                                .status(CommonStatus.DISABLED)
                                                .build(),
                                        null))
                        .compose(id ->
                                funSQLClient.save(
                                        Account.builder()
                                                .name("idealworld2")
                                                .openId("yyyy")
                                                .status(CommonStatus.DISABLED)
                                                .build(),
                                        null
                                ))
                        .compose(resp ->
                                funSQLClient.list("select * from account where name like #{name}", new HashMap<>() {
                                    {
                                        put("name", "idealworld%");
                                    }
                                }, Account.class, null))
        )
                .onComplete(resp -> {
                    Assertions.assertEquals(2, resp.result().size());
                    Assertions.assertEquals("idealworld1", resp.result().get(0).name);
                    funSQLClient.list("select * from account where name like #{name}", new HashMap<>() {
                        {
                            put("name", "idealworld%");
                        }
                    }, Account.class, null)
                            .onComplete(r -> {
                                Assertions.assertEquals(2, r.result().size());
                                Assertions.assertEquals("idealworld1", r.result().get(0).name);
                                Assertions.assertEquals(CommonStatus.DISABLED, r.result().get(0).status);
                                testContext.completeNow();
                            });
                });
    }

    @Test
    @Order(5)
    public void testTXFail(Vertx vertx, VertxTestContext testContext) {
        funSQLClient.tx(client ->
                Future.succeededFuture()
                        .compose(resp ->
                                client.save(
                                        Account.builder()
                                                .name("idealworld3")
                                                .openId("yyyy")
                                                .status(CommonStatus.DISABLED)
                                                .build(),
                                        null))
                        .compose(id ->
                                client.save(
                                        Account.builder()
                                                .name("idealworld3")
                                                .openId("yyyy")
                                                .status(CommonStatus.DISABLED)
                                                .build(),
                                        null
                                ))
                        .compose(resp ->
                                client.list("select * from account where name like #{name}", new HashMap<>() {
                                    {
                                        put("name", "idealworld%");
                                    }
                                }, Account.class, null))
        )
                .onComplete(resp -> {
                    Assertions.assertTrue(resp.failed());
                    funSQLClient.list("select * from account where name like #{name}", new HashMap<>() {
                        {
                            put("name", "idealworld%");
                        }
                    }, Account.class, null)
                            .onComplete(r -> {
                                Assertions.assertEquals(2, r.result().size());
                                Assertions.assertEquals("idealworld1", r.result().get(0).name);
                                Assertions.assertEquals("idealworld2", r.result().get(1).name);
                                testContext.completeNow();
                            });
                });
    }

    @Test
    @Order(6)
    public void testTXFailReentrant(Vertx vertx, VertxTestContext testContext) {
        funSQLClient.tx(client ->
                Future.succeededFuture()
                        .compose(resp ->
                                client.save(
                                        Account.builder()
                                                .name("idealworld3")
                                                .openId("yyyy")
                                                .status(CommonStatus.DISABLED)
                                                .build(),
                                        null))
                        .compose(id ->
                                client.tx(clientInner -> clientInner.save(
                                        Account.builder()
                                                .name("idealworld3")
                                                .openId("yyyy")
                                                .status(CommonStatus.DISABLED)
                                                .build(),
                                        null
                                ))
                        )
                        .compose(resp ->
                                client.list("select * from account where name like #{name}", new HashMap<>() {
                                    {
                                        put("name", "idealworld%");
                                    }
                                }, Account.class, null))
        )
                .onComplete(resp -> {
                    Assertions.assertTrue(resp.failed());
                    funSQLClient.list("select * from account where name like #{name}", new HashMap<>() {
                        {
                            put("name", "idealworld%");
                        }
                    }, Account.class, null)
                            .onComplete(r -> {
                                Assertions.assertEquals(2, r.result().size());
                                Assertions.assertEquals("idealworld1", r.result().get(0).name);
                                Assertions.assertEquals("idealworld2", r.result().get(1).name);
                                testContext.completeNow();
                            });
                });
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Account extends IdEntity {

        private String name;
        private String openId;
        private CommonStatus status;

    }

}
