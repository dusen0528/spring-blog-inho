package com.nhnacademy.blog.common.context;

import com.nhnacademy.blog.common.db.DbProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ContextHolderTest {

    @Test
    void getApplicationContext() throws SQLException {
        ApplicationContext applicationContext = ContextHolder.getApplicationContext();

        DbProperties dbProperties = (DbProperties) applicationContext.getBean("dbProperties");
        log.debug("dbProperties:{}", dbProperties);

        DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
        log.debug("dataSource:{}", dataSource);

        Connection connection = dataSource.getConnection();
    }

}