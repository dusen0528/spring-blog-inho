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

/**
 * TODO#5 database Connection Pool을 생성하는 Initializeable을 구현한 구현체 입니다.
 * @InitOrder(value = 2) 설정되어 있어서 Application Context에 의해서 2번째째로 initialize() method가 호출 됩니다.
 * InitDataSource 구현하세요.
 */

@Slf4j
@InitOrder(value = 2)
public class InitDataSource implements Initializeable {
    //TODO#5-1 BEAN_NAME='dataSource'로 설정하세요
    public static final String BEAN_NAME = "dataSource";

    @Override
    public synchronized void initialize(Context context) {
//        //TODO#5-2 context로부터 DbProperties.BEAN_NAME을 사용하여 해당 데이터베이스 환경 설정 정보를 담고 있는 DbProperties 객체를 얻습니다.
//        DbProperties dbProperties = (DbProperties) context.getBean(DbProperties.BEAN_NAME);
//        /* TODO#5-3 createDataSource를 호출해서 DataSource 객체를 생성 합니다
//
//        */
//        DataSource dataSource = createDataSource(dbProperties);
//
//        //TODO#5-4 dataSouce를 Conect에 registerBean() 메서드를 사용하여 다음과 같이 등록 합니다.
//        //name = BEAN_NAME, object = dataSource
//        context.registerBean(BEAN_NAME,dataSource);
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
