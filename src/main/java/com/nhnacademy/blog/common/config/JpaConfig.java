package com.nhnacademy.blog.common.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration

/**
 * @EnableJpaRepositories 애노테이션은 Spring Data JPA 리포지토리를 활성화하는 역할을 합니다.
 * 이 애노테이션을 사용하면 JPA 리포지토리 인터페이스()가 Spring 애플리케이션 컨텍스트에 자동으로 등록됩니다.
 */
@EnableJpaRepositories(
        // basePackageClasses 속성은 스캔할 리포지토리 인터페이스가 포함된 패키지를 지정합니다.
        // com.nhnacademy.blog.RootPackageBase 클래스가 위치한 패키지를 기준으로 리포지토리 인터페이스를 찾습니다.
        basePackageClasses = {com.nhnacademy.blog.RootPackageBase.class},
        // transactionManagerRef 속성은 사용할 트랜잭션 매니저 빈의 이름을 지정합니다.
        // 여기서는 "jpaTransactionManager"라는 이름의 트랜잭션 매니저를 사용합니다.
        transactionManagerRef ="jpaTransactionManager",
        // entityManagerFactoryRef 속성은 사용할 엔티티 매니저 팩토리 빈의 이름을 지정합니다.
        // 여기서는 "jpaEntityManagerFactory"라는 이름의 엔티티 매니저 팩토리를 사용합니다.
        entityManagerFactoryRef = "jpaEntityManagerFactory"
)

//Spring의 트랜잭션 관리 기능을 활성화합니다. 이를 통해 메서드에서 트랜잭션을 선언적으로 관리할 수 있습니다.
@EnableTransactionManagement
public class JpaConfig {

    /**
     * LocalContainerEntityManagerFactoryBean을 설정합니다.
     * 이 메서드는 데이터 소스를 기반으로 엔티티 매니저 팩토리를 생성합니다.
     * @param dataSource , Application Context에 등록된  dataSource Bean 주입됩니다.
     * @return
     */
    @Bean(name = "jpaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean jpaEntityManagerFactory(DataSource dataSource, Map<String,String> jpaPropertyMap) {
        log.debug("configJpaPropertyMap:{}", jpaPropertyMap);

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("com.nhnacademy.blog");  // 엔티티 클래스가 위치한 패키지 설정

        // Hibernate 관련 설정
        factoryBean.setJpaPropertyMap(jpaPropertyMap);
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factoryBean.setPersistenceProviderClass(org.hibernate.jpa.HibernatePersistenceProvider.class);

        return factoryBean;
    }

    /**
     * test(테스트) 환경에서의 jpaPropertyMap
     *  - test(테스트) 환경에서의 jpa설정
     *  - @Profile("test") <-- test profile에서만 Bean 생성됨.
     * @return
     */
    @Bean("jpaPropertyMap")
    @Profile("test")
    public Map<String, String> testJpaPropertyMap() {
        Map<String, String> map = new HashMap<>();

        // Hibernate가 사용할 방언(dialect)을 설정합니다.
        // 여기서는 H2 데이터베이스 방언을 사용합니다.
        map.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        // Hibernate가 데이터베이스 스키마를 처리하는 방식을 설정합니다.
        // 'create-drop'은 애플리케이션 시작 시 스키마를 생성하고, 종료 시 삭제합니다.
        map.put("hibernate.hbm2ddl.auto", "create-drop");

        // SQL 쿼리를 로그에 출력할지 여부를 설정합니다.
        // 'true'로 설정하면 SQL 쿼리가 로그에 출력됩니다.
        map.put("hibernate.show_sql", "true");

        // SQL 쿼리를 포맷할지 여부를 설정합니다.
        // 'true'로 설정하면 SQL 쿼리가 포맷되어 읽기 쉽게 정렬됩니다.
        map.put("hibernate.format_sql", "true");

        //ANSI 이스케이프 코드를 통해 구문 강조 표시와 함께 SQL을 기록합니다 .
        map.put("hibernate.highlight_sql", "true");

        // SQL 쿼리에 주석을 추가할지 여부를 설정합니다.
        map.put("hibernate.use_sql_comments", "true");

        //CamelCase를 snake_case로 변경 한다.
        //모든 문자를 소문자로 변경 한다.
        map.put("hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy.class.getName());

        //parameter 출력
        //[참고] https://docs.jboss.org/hibernate/orm/6.6/userguide/html_single/Hibernate_User_Guide.html#best-practices-logging
        map.put("logging.level.org.hibernate.orm", "trace");

        return map;
    }


    /**
     * prod(운영) 환경에서의 jpaPropertyMap
     *  - prod(운영) 환경에서의 jpa설정
     *  - @Profile("prod") <-- prod profile에서만 Bean 생성됨.
     * @return
     */
    @Bean("jpaPropertyMap")
    @Profile("prod")
    public Map<String, String> prodJpaPropertyMap() {
        Map<String, String> map = new HashMap<>();

        // Hibernate가 사용할 방언(dialect)을 설정합니다.
        // 여기서는 MySQL 데이터베이스 방언을 사용합니다.
        map.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

        // Hibernate가 데이터베이스 스키마를 처리하는 방식을 설정합니다.
        // 'validate'는 엔티티와 테이블 구조가 일치하는지 확인하지만, 실제로 변경하지는 않습니다.
        map.put("hibernate.hbm2ddl.auto", "validate");

        // SQL 쿼리를 로그에 출력할지 여부를 설정합니다.
        // 'false'로 설정하면 SQL 쿼리가 로그에 출력되지 않습니다.
        map.put("hibernate.show_sql", "false");

        // SQL 쿼리를 포맷할지 여부를 설정합니다.
        // 'false'로 설정하면 SQL 쿼리가 포맷되지 않습니다.
        map.put("hibernate.format_sql", "false");

        //ANSI 이스케이프 코드를 통해 구문 강조 표시와 함께 SQL을 기록합니다 .
        map.put("hibernate.highlight_sql", "false");

        // SQL 쿼리에 주석을 추가할지 여부를 설정합니다.
        // 'false'로 설정하면 SQL 쿼리에 주석이 추가되지 않습니다.
        map.put("hibernate.use_sql_comments", "false");

        //CamelCase를 snake_case로 변경 한다.
        //모든 문자를 소문자로 변경 한다.
        map.put("hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy.class.getName());


        return map;
    }

    /**
     * 트랜잭션 매니저 설정
     * - jdbc : DataSourceTransactionManager
     * - jpa : JpaTransactionManager
     * @param entityManagerFactory
     * @return
     */
    @Bean
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public PhysicalNamingStrategy physical() {
        return new PhysicalNamingStrategyStandardImpl();
    }

    @Bean
    public ImplicitNamingStrategy implicit() {
        return new ImplicitNamingStrategyLegacyJpaImpl();
    }
}
