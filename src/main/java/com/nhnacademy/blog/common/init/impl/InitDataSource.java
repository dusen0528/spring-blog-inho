package com.nhnacademy.blog.common.init.impl;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.db.DbProperties;
import com.nhnacademy.blog.common.init.Initializeable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.time.Duration;

@Slf4j
@InitOrder(value = 2)
public class InitDataSource implements Initializeable {
    public static final String BEAN_NAME = "dataSource";
    
    @Override
    public synchronized void initialize(Context context) {
        DbProperties dbProperties = (DbProperties) context.getBean(DbProperties.BEAN_NAME);
        DataSource dataSource = createDataSourceByC3p0(dbProperties);
        context.registerBean(BEAN_NAME,dataSource);
    }

    private DataSource createDataSource(DbProperties dbProperties){
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
        return basicDataSource;
    }

    private DataSource createDataSourceByC3p0(DbProperties dbProperties){
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 클래스
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
        dataSource.setJdbcUrl(dbProperties.getUrl()); // JDBC URL
        dataSource.setUser(dbProperties.getUsername()); // 데이터베이스 사용자 이름
        dataSource.setPassword(dbProperties.getPassword()); // 데이터베이스 비밀번호
        dataSource.setInitialPoolSize(dbProperties.getInitialSize());

        // 추가 설정
        dataSource.setMinPoolSize(dbProperties.getMinIdle());
        dataSource.setMaxPoolSize(dbProperties.getInitialSize());
        dataSource.setMaxIdleTime(dbProperties.getMaxIdle());
        dataSource.setTestConnectionOnCheckin(dbProperties.isTestOnBorrow());
        dataSource.setPreferredTestQuery(dbProperties.getPassword());
        return dataSource;
    }

}
