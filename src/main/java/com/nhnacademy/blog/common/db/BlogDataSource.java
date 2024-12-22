package com.nhnacademy.blog.common.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.annotation.Qualifier;
import com.nhnacademy.blog.common.annotation.stereotype.Component;
import com.p6spy.engine.spy.P6DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.time.Duration;

@InitOrder(Integer.MIN_VALUE)
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


    public DataSource getDataSource() {
        return dataSource;
    }

}
