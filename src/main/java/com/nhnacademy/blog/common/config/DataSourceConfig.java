package com.nhnacademy.blog.common.config;

import com.nhnacademy.blog.common.db.DbProperties;
import com.p6spy.engine.spy.P6DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Duration;

/**
 * DataSource Bean 생성
 *
 * 이 클래스는 데이터베이스 연결에 필요한 DataSource를 설정하는 클래스입니다.
 * Spring의 @Configuration 어노테이션을 사용하여 Spring IoC 컨테이너에 설정 정보를 제공하며,
 * @Bean 어노테이션을 사용하여 DataSource 빈을 생성하고 이를 Spring 컨테이너에 등록합니다.
 *
 * @Configuration:
 * - 이 어노테이션은 해당 클래스가 "설정 클래스"임을 나타냅니다.
 * - 설정 클래스는 Spring IoC 컨테이너에 빈을 정의하는 데 사용됩니다.
 * - 이 어노테이션이 붙은 클래스는 Spring 컨테이너에 의해 자동으로 관리되며,
 *   이 클래스 내부에서 정의된 @Bean 메소드들은 Spring 컨테이너에 빈으로 등록됩니다.
 * - @Configuration을 사용하면 해당 클래스는 애플리케이션의 설정을 포함하는 역할을 하며,
 *   다른 클래스에서 필요한 빈을 생성하거나, 설정 값을 주입하는 등의 작업을 수행합니다.
 *
 * 예를 들어, @Configuration을 통해 데이터베이스 연결 설정을 포함한 DataSource 빈을 정의하거나,
 * 서비스, 레포지토리, 엔티티 등의 다른 빈들을 설정하는 데 사용될 수 있습니다.
 */

@Slf4j
@Configuration
public class DataSourceConfig {

    /**
     * @Bean 어노테이션은 해당 메소드가 반환하는 객체를 Spring의 빈(Bean)으로 등록하겠다는 의미입니다.
     * 이 메소드에서는 DataSource를 생성하고, 필요한 설정을 적용하여 반환합니다.
     * DataSource는 데이터베이스와의 연결을 관리하는 객체로, 애플리케이션에서 DB에 접근하는 데 사용됩니다.
     *
     * @param dbProperties 데이터베이스 관련 속성값을 담고 있는 DbProperties 객체
     *                    이 객체는 Spring에서 자동으로 주입됩니다.
     *                    DbProperties가 빈으로 등록되어 있기 때문에 별도로 설정하지 않아도
     *                    자동으로 Spring 컨테이너가 주입합니다.
     *                    DbProperties 클래스에는 @ConfigurationProperties 또는 @Component 등의 어노테이션이 붙어 있어
     *                    Spring IoC 컨테이너에 의해 관리되며, DataSourceConfig에서 자동으로 주입됩니다.
     * @return 설정된 DataSource 객체
     */

    //TODO#8 - dataSource Bean 생성
    @Bean
    public DataSource dataSource(DbProperties dbProperties){
        log.debug("datasource-dbProperties: {}", dbProperties);

        // 기본적인 데이터소스 설정
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

        // Spy 기능 활성화 여부에 따라 P6Spy 데이터소스 적용
        return dbProperties.isSpy() ? createP6SpyDataSource(basicDataSource) : basicDataSource;
    }

    /**
     * P6Spy를 사용하여 SQL 쿼리 로그를 기록할 수 있는 DataSource를 생성합니다.
     * @param dataSource 기존의 DataSource
     * @return P6Spy를 적용한 DataSource
     */
    private DataSource createP6SpyDataSource(DataSource dataSource){
        return new P6DataSource(dataSource);
    }

}
