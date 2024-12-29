package com.nhnacademy.blog.common.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration

/**
 * TODO#9 - @EnableJpaRepositories 애노테이션은 Spring Data JPA 리포지토리를 활성화하는 역할을 합니다.
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
     * TODO#10 - LocalContainerEntityManagerFactoryBean을 설정합니다.
     * 이 메서드는 데이터 소스를 기반으로 엔티티 매니저 팩토리를 생성합니다.
     * @param dataSource , Application Context에 등록된  dataSource Bean 주입됩니다.
     * @return
     */
    @Bean(name = "jpaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean jpaEntityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("com.nhnacademy.blog");  // 엔티티 클래스가 위치한 패키지 설정

        // Hibernate 관련 설정
        factoryBean.getJpaPropertyMap().put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        factoryBean.getJpaPropertyMap().put("hibernate.hbm2ddl.auto", "create-drop");
        factoryBean.getJpaPropertyMap().put("hibernate.show_sql", "true");
        factoryBean.getJpaPropertyMap().put("hibernate.format_sql", "true");
        factoryBean.getJpaPropertyMap().put("hibernate.use_sql_comments", "true");
        factoryBean.getJpaPropertyMap().put("hibernate.naming.physical-strategy", org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl.class.getName());
        factoryBean.getJpaPropertyMap().put("hibernate.naming.impersonalize-strategy", org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl.class.getName());
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factoryBean.setPersistenceProviderClass(org.hibernate.jpa.HibernatePersistenceProvider.class);

        return factoryBean;
    }

    /**
     * TODO#11 - 트랜잭션 매니저 설정
     * - jdbc : DataSourceTransactionManager
     * - jpa : JpaTransactionManager
     * @param entityManagerFactory
     * @return
     */
    @Bean
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
