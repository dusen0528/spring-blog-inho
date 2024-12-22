package com.nhnacademy.blog.common.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.annotation.Qualifier;
import com.nhnacademy.blog.common.annotation.stereotype.Component;
import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.p6spy.engine.spy.P6DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.time.Duration;

@InitOrder(Integer.MIN_VALUE)
/* TODO#6-1
    @Component(value="beanName") <-- Compoent를 선언 합니다.
     - value에 대해되는 beanName은 BlogDataSource.BEAN_NAME을 사용합니다.(  public static final String BEAN_NAME 선언하는 이유: 명시적 선언 및 오타방지)
     - Component는 Application Context에 의해서 Bean(객체)로 등록 됩니다.
     - Application Context가 관리하는 객체를 Bean이라고 합니다.
*/
@Component(BlogDataSource.BEAN_NAME )
public class BlogDataSource {
    //TODO#6-2 BEAN_NAME 변경하세요.
    public static final String BEAN_NAME="dataSource";

    private final DbProperties dbProperties;
    private final DataSource dataSource;

    public BlogDataSource(
            //todo#6-3 @Qualifier(DbProperties.BEAN_NAME)을 사용하여  DbProperties dbProperties에 Application Context에 등록된 DbProperties.BEAN_NAME 해당되는 Bean을 주입 받습니다.
            @Qualifier(DbProperties.BEAN_NAME) DbProperties dbProperties
    ) {
        //TODO#6-4 this.dbProperties를 초기화 합니다.
        this.dbProperties = dbProperties;

        /*TODO#6-5 dbProperties.isSpy()==true면 createP6SpyDataSource()를 호출 합니다.
           - dbProperties.isSpy()==false면 createDataSource()를 호출 합니다.
         */
        this.dataSource = dbProperties.isSpy() ? createP6SpyDataSource(createDataSource()) : createDataSource();
    }

    private DataSource createDataSource(){
        /*TODO#6-6 BasicDataSource 객체를 생성 합니다.
            - BasicDataSource 객체 생성시 필요한 환경설정 값은 dbProperties 객체를 기반으로 설정 합니다.
        */

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
        //TODO#6-7 dataSource를 이용해서 P6DataSource 객체를 생성 합니다.
        return new P6DataSource(dataSource);
    }

    private DataSource createDataSourceByC3p0(DbProperties dbProperties){
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 클래스
        } catch (PropertyVetoException e) {
            throw new DatabaseException(e);
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
        //TODO#6-8 dataSource를 반환 합니다.
        return dataSource;
    }

}
