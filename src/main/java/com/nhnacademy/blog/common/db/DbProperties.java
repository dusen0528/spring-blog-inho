package com.nhnacademy.blog.common.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * TODO#1-6 db.properties 파일을 기반으로 데이터베이스 접속 정보 객체화
 *
 * 이 클래스는 db.properties 파일에 정의된 데이터베이스 접속 정보를 바탕으로
 * 해당 정보를 객체로 변환하는 역할을 합니다.
 * Spring에서 제공하는 @Configuration과 @PropertySource 어노테이션을 사용하여
 * 프로퍼티 파일을 로드하고, @Value 어노테이션을 통해 해당 파일의 값을 클래스의 필드에 주입합니다.
 */
@SuppressWarnings("java:S107")
@Configuration
@PropertySource("classpath:db.properties") // db.properties 파일을 로드
public class DbProperties {

    // 기본 Bean 이름을 설정합니다.
    public static final String BEAN_NAME = "dbProperties";

    @Value("${db.url}")
    private String url; // 데이터베이스 URL
    @Value("${db.username}")
    private String username; // 데이터베이스 사용자명
    @Value("${db.password}")
    private String password; // 데이터베이스 비밀번호
    @Value("${db.initialSize}")
    private int initialSize; // 초기 커넥션 풀 크기
    @Value("${db.maxTotal}")
    private int maxTotal; // 최대 커넥션 풀 크기
    @Value("${db.maxIdle}")
    private int maxIdle; // 최대 유휴 커넥션 수
    @Value("${db.minIdle}")
    private int minIdle; // 최소 유휴 커넥션 수
    @Value("${db.maxWait}")
    private int maxWait; // 커넥션 대기 시간
    @Value("${db.validationQuery}")
    private String validationQuery; // 커넥션 검증 쿼리
    @Value("${db.testOnBorrow}")
    private boolean testOnBorrow; // 커넥션 풀에서 커넥션을 빌릴 때 검증 여부
    @Value("${db.spy}")
    private boolean spy; // P6Spy 로깅 사용 여부

    // getter 메소드 구현. setter 메소드는 구현하지 않음.

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

    // toString() 메서드를 오버라이드하여 DbProperties 객체를 문자열로 출력
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
