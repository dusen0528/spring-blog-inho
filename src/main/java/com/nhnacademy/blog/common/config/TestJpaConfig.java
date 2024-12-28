package com.nhnacademy.blog.common.config;

import com.p6spy.engine.spy.P6DataSource;
import jakarta.persistence.EntityManagerFactory;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackageClasses = {com.nhnacademy.blog.RootPackageBase.class},
        transactionManagerRef ="jpaTransactionManager",
        entityManagerFactoryRef = "jpaEntityManagerFactory"
)
@EnableTransactionManagement
public class TestJpaConfig {

    @Primary
    @Bean(name = "h2Datasource")
    public DataSource h2Datasource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb");
        dataSource.setUsername("sa");
        dataSource.setPassword("password");
        return new P6DataSource(dataSource);
    }

    // LocalContainerEntityManagerFactoryBean 설정
    @Bean(name = "jpaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean jpaEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(h2Datasource());
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

    // 트랜잭션 매니저 설정
    @Bean
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
