package com.nhnacademy.blog.common.config;

import com.nhnacademy.blog.RootPackageBase;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration

/**
 * spring Data JPA에서 JPA 리포지토리 인터페이스를 자동으로 검색하고,
 * Spring 컨텍스트에 빈으로 등록하기 위해 사용되는 애노테이션입니다.
 * 이 애노테이션을 사용하면 지정된 패키지에서 JPA 리포지토리를 찾아 자동으로 구성할 수 있습니다
 */
@EnableJpaRepositories(
        basePackageClasses = RootPackageBase.class,
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "jpaTransactionManager"
)
public class JpaConfig {

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);

        // JPA 엔티티 클래스가 위치한 패키지 설정
        entityManagerFactory.setPackagesToScan("com.nhnacademy.blog");
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return entityManagerFactory;
    }

    /**
     * Spring 프레임워크에서 JPA 기반의 트랜잭션을 관리하기 위해 사용되는 트랜잭션 관리자입니다.
     * 이 클래스는 JPA 기반의 애플리케이션에서 트랜잭션을 처리하고 관리하는 역할을 합니다.
     * Spring의 트랜잭션 관리 추상화를 사용하여, 애플리케이션 개발자가 데이터 액세스 로직에서
     * 명시적으로 트랜잭션을 관리하지 않도록 도와줍니다
     *
     * @param entityManagerFactory
     * @return
     */
    @Bean(name = "jpaTransactionManager")
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
