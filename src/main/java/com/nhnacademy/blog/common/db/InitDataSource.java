package com.nhnacademy.blog.common.db;

import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.init.Initializeable;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.time.Duration;

@InitOrder(value = 2)
public class InitDataSource implements Initializeable {
    public static final String BEAN_NAME = "dataSource";

    @Override
    public void initialize() {
        Context context = ContextHolder.getApplicationContext();
        DbProperties dbProperties = (DbProperties) context.getBean(DbProperties.BEAN_NAME);
        DataSource dataSource = createDataSource(dbProperties);
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
}
