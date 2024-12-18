package com.nhnacademy.blog.common.db;

import com.nhnacademy.blog.common.annotation.Qualifier;
import com.nhnacademy.blog.common.annotation.stereotype.Component;
import com.p6spy.engine.spy.P6DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;
import java.time.Duration;

@Component(BlogDataSource.BEAN_NAME )
public class BlogDataSource {
    public static final String BEAN_NAME="dataSource";

    private final DbProperties dbProperties;
    private final DataSource dataSource;

    public BlogDataSource(@Qualifier(DbProperties.BEAN_NAME) DbProperties dbProperties) {
        this.dbProperties = dbProperties;
        this.dataSource = dbProperties.isSpy() ? createP6SpyDataSource(createDataSource()) : createDataSource();
    }

    private DataSource createDataSource(){
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

    private DataSource createP6SpyDataSource(DataSource dataSource){
        return new P6DataSource(dataSource);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

}
