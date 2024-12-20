package com.nhnacademy.blog.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;

public class Dbcp2 {

    @Test
    void test() throws SQLException {

        BasicDataSource basicDataSource = new BasicDataSource();

        basicDataSource.setUrl("jdbc:mysql://133.186.241.167:3306/nhn_academy_blog");
        basicDataSource.setUsername("nhn_academy_blog");
        basicDataSource.setPassword("Phv.GkNm5l7B_GHV");

        basicDataSource.setInitialSize(1);
        basicDataSource.setMaxTotal(5);
        basicDataSource.setMaxIdle(5);
        basicDataSource.setMinIdle(1);
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        basicDataSource.setMaxWait(Duration.ofSeconds(2));
        basicDataSource.setValidationQuery("SELECT 1");
        basicDataSource.setTestOnBorrow(true);

        Connection connection = basicDataSource.getConnection();

    }

    @Test
    void test2() throws PropertyVetoException, SQLException {

        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 클래스
        dataSource.setJdbcUrl("jdbc:mysql://133.186.241.167:3306/nhn_academy_blog"); // JDBC URL
        dataSource.setUser("nhn_academy_blog"); // 데이터베이스 사용자 이름
        dataSource.setPassword("Phv.GkNm5l7B_GHV"); // 데이터베이스 비밀번호
        dataSource.setInitialPoolSize(5);

        // 추가 설정
        dataSource.setMinPoolSize(5);
        dataSource.setAcquireIncrement(5);
        dataSource.setMaxPoolSize(5);
        dataSource.setMaxIdleTime(5);
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setPreferredTestQuery("SELECT 1");
        Connection connection = dataSource.getConnection();

    }
}
