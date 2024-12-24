package com.nhnacademy.blog.common.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PropertyKey;

@SuppressWarnings("java:S107")
@Configuration
@PropertySource("classpath:db.properties")
public class DbProperties {
    /**
     * TODO#4-6 DbProperties 객체를 구현 합니다.
     */

    //기본 BEAN_NAME을 설정 합니다.
    public static final String BEAN_NAME="dbProperties";

    @Value("${db.url}")
    private String url;
    @Value("${db.username}")
    private String username;
    @Value("${db.password}")
    private String password;
    @Value("${db.initialSize}")
    private int initialSize;
    @Value("${db.maxTotal}")
    private int maxTotal;
    @Value("${db.maxIdle}")
    private int maxIdle;
    @Value("${db.minIdle}")
    private int minIdle;
    @Value("${db.maxWait}")
    private int maxWait;
    @Value("${db.validationQuery}")
    private String validationQuery;
    @Value("${db.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${db.spy}")
    private boolean spy;

    //getter method를 구현 합니다. setter method는 구현하지 않습니다.

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public boolean isSpy() {
        return spy;
    }

    //toString()메서드를 override 하여 구현합니다.
    @Override
    public String toString() {
        return "DbProperties{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", initialSize=" + initialSize +
                ", maxTotal=" + maxTotal +
                ", maxIdle=" + maxIdle +
                ", minIdle=" + minIdle +
                ", maxWait=" + maxWait +
                ", validationQuery='" + validationQuery + '\'' +
                ", testOnBorrow=" + testOnBorrow +
                ", spy=" + spy +
                '}';
    }
}
