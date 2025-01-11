package com.nhnacademy.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
//TODO#18 - dbcp2 환경설정이 잘  되었다면 아래 테스코드가 통화 합니다.
//아래 테스트 코드는 운영환경을 기준으로 테스트 합니다.

@Slf4j
@SpringBootTest
@ActiveProfiles("prod")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class ProdDataSourceTest {
    @Autowired
    DataSource dataSource;

    private  boolean isDBCP2(){
        return dataSource instanceof BasicDataSource;
    }
    @Order(1)
    @Test
    @DisplayName("운영환경(prod) - commons dbcp2 사용여부")
    void useBaiscDataSource(){
        Assertions.assertThat(isDBCP2()).isTrue();
    }
    @Order(2)
    @Test
    @DisplayName("운영환경(prod) - Driver Class Name : mysql")
    void useMySql(){
        assumeTrue(isDBCP2());
        BasicDataSource ds = (BasicDataSource) dataSource;
        Assertions.assertThat(ds.getDriverClassName()).contains("mysql");
    }
    @Order(3)
    @Test
    @DisplayName("운영환경(prod) - testOnBorrow = true")
    void testOnBorrow(){
        assumeTrue(isDBCP2());
        BasicDataSource ds = (BasicDataSource) dataSource;
        Assertions.assertThat(ds.getTestOnBorrow()).isTrue();
    }
    @Order(4)
    @Test
    @DisplayName("운영환경(prod) - validationQuery = true")
    void useValidationQuery(){
        assumeTrue(isDBCP2());
        BasicDataSource ds = (BasicDataSource) dataSource;
        Assertions.assertThat(ds.getValidationQuery().toLowerCase().trim()).contains("select 1");
    }
    @Order(5)
    @Test
    @DisplayName("운영환경(prod) -  MaxIdle == MinIdle == InitialSize == MaxTotal")
    void dbcpOptimization(){
        assumeTrue(isDBCP2());
        BasicDataSource ds = (BasicDataSource) dataSource;

        log.debug("maxIdle : {}", ds.getMaxIdle());
        log.debug("minIdle : {}", ds.getMinIdle());
        log.debug("initialSize : {}", ds.getInitialSize());
        log.debug("maxTotal : {}", ds.getMaxTotal());

        Assertions.assertThat(ds.getMaxIdle())
                .isEqualTo(ds.getMinIdle())
                .isEqualTo(ds.getInitialSize())
                .isEqualTo(ds.getMaxTotal());
    }

}
