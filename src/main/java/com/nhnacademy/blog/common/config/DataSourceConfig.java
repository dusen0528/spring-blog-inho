package com.nhnacademy.blog.common.config;

import com.nhnacademy.blog.common.db.DbProperties;
import com.p6spy.engine.spy.P6DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Duration;
@Slf4j
@Configuration
public class DataSourceConfig {

    @Bean("dataSource")
    public DataSource dataSource(DbProperties dbProperties){
        log.debug("datasource-dbProperties: {}", dbProperties);
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(dbProperties.getUrl());
        basicDataSource.setUsername(dbProperties.getUsername());
        basicDataSource.setPassword(dbProperties.getPassword());

        basicDataSource.setInitialSize(dbProperties.getInitialSize());
        basicDataSource.setMaxTotal(dbProperties.getMaxTotal());
        basicDataSource.setMaxIdle(dbProperties.getMaxIdle());
        basicDataSource.setMinIdle(dbProperties.getMinIdle());

        basicDataSource.setMaxWait(Duration.ofSeconds(dbProperties.getMaxWait()));
        basicDataSource.setValidationQuery(dbProperties.getValidationQuery());
        basicDataSource.setTestOnBorrow(dbProperties.isTestOnBorrow());
        basicDataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        return dbProperties.isSpy() ? createP6SpyDataSource(basicDataSource) : basicDataSource;
    }

    private DataSource createP6SpyDataSource(DataSource dataSource){
        return new P6DataSource(dataSource);
    }

}
