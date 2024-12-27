package com.nhnacademy.blog.common.context;

import com.nhnacademy.blog.common.db.DbProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
class ContextHolderTest {

    @Test
    @DisplayName("load context")
    void getApplicationContext() throws SQLException {
        ApplicationContext applicationContext = ContextHolder.getApplicationContext();

        DbProperties dbProperties = (DbProperties) applicationContext.getBean("dbProperties");
        log.debug("dbProperties:{}", dbProperties);

        DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
        log.debug("dataSource:{}", dataSource);

        Assertions.assertNotNull(dbProperties);
        Assertions.assertNotNull(dataSource);
    }

}