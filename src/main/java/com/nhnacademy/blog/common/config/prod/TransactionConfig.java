package com.nhnacademy.blog.common.config.prod;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


/**
 * Transaction 관리
 *
 * 이 클래스는 데이터베이스의 트랜잭션 관리를 설정합니다.
 * @Configuration: 해당 클래스가 스프링의 설정 클래스임을 나타냅니다.
 * @EnableTransactionManagement: 트랜잭션 관리를 활성화하고, 트랜잭션 관련 어노테이션(@Transactional)을 사용할 수 있게 합니다.
 * 트랜잭션 관리는 데이터베이스 작업의 일관성을 보장하기 위해 필요하며, 데이터베이스 연산 중 문제가 발생했을 때 롤백할 수 있도록 도와줍니다.
 */
@Configuration
@EnableTransactionManagement // 트랜잭션 관리 기능을 활성화하는 어노테이션
public class TransactionConfig {

    /**
     * 트랜잭션 매니저를 설정하는 메서드입니다.
     * DataSource를 사용하여 DataSourceTransactionManager를 생성하여 반환합니다.
     * DataSourceTransactionManager는 JDBC 데이터베이스 트랜잭션을 관리하는 구현체입니다.
     * @param dataSource 데이터베이스 연결 정보를 담고 있는 DataSource 객체
     * @return PlatformTransactionManager 객체
     *
     * PlatformTransactionManager는 스프링의 트랜잭션 관리 인터페이스로,
     * 여러 트랜잭션 매니저 구현체가 이 인터페이스를 구현하여 사용됩니다.
     * DataSourceTransactionManager는 JDBC 트랜잭션을 처리할 때 사용하는 구현체입니다.
     *
     * 트랜잭션 매니저는 트랜잭션을 시작, 커밋, 롤백하는 책임을 담당합니다.
     * 즉, 데이터베이스의 트랜잭션을 관리하여, 여러 개의 데이터베이스 작업을 하나의 트랜잭션으로 묶고,
     * 모든 작업이 성공적으로 완료되면 커밋하고, 오류가 발생하면 롤백하여 데이터의 일관성을 보장합니다.
     */
    @Profile("prod")
    @Bean
    PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource); // DataSource를 사용한 트랜잭션 매니저
    }

    /**
     * JdbcTemplate을 설정하는 메서드입니다.
     * JdbcTemplate은 SQL 쿼리 실행을 간소화해 주는 Spring의 유틸리티 클래스입니다.
     * SQL 쿼리를 실행하고, 그 결과를 처리하는 복잡한 작업을 대신 처리해 줍니다.
     * @param dataSource 데이터베이스 연결 정보를 담고 있는 DataSource 객체
     * @return JdbcTemplate 객체
     *
     * JdbcTemplate의 주요 기능:
     * 1. SQL 쿼리 실행: SQL 쿼리, 업데이트 또는 조회를 실행할 수 있습니다.
     * 2. 결과 매핑: 데이터베이스 쿼리 결과를 객체로 매핑할 수 있습니다. 예를 들어, ResultSet을 특정 객체에 매핑할 때 사용됩니다.
     * 3. 예외 처리: SQLException과 같은 JDBC 예외를 런타임 예외로 변환하여 처리할 수 있습니다.
     * 4. 리소스 관리: 데이터베이스 연결, Statement, ResultSet 등 리소스를 자동으로 관리하고 종료시켜 줍니다.
     * 5. Batch 처리: 대량의 데이터를 처리할 때 유용한 배치 처리를 지원합니다.
     *
     * JdbcTemplate은 반복적인 JDBC 코드를 간소화하고, 자원 관리 및 예외 처리를 자동화하여 개발자가 쉽게 사용할 수 있도록 도와줍니다.
     */
    
    @Profile("prod")
    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource); // DataSource를 사용하여 JdbcTemplate 객체 생성
    }
}

