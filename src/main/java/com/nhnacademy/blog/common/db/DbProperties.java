package com.nhnacademy.blog.common.db;

@SuppressWarnings("java:S107")
public class DbProperties {
    /**
     * TODO#4-6 DbProperties 객체를 구현 합니다.
     */

    //기본 BEAN_NAME을 설정 합니다.
    public static final String BEAN_NAME="dbProperties";

    private final String url;
    private final String username;
    private final String password;

    private final int initialSize;
    private final int maxTotal;
    private final int maxIdle;
    private final int minIdle;

    private final int maxWait;
    private final String validationQuery;
    private final boolean testOnBorrow;
    private final boolean spy;

    public DbProperties(String url, String username, String password, int initialSize, int maxTotal, int maxIdle, int minIdle, int maxWait, String validationQuery, boolean testOnBorrow, boolean spy) {
        //생성자를 구현하세요.

        this.url = url;
        this.username = username;
        this.password = password;
        this.initialSize = initialSize;
        this.maxTotal = maxTotal;
        this.maxIdle = maxIdle;
        this.minIdle = minIdle;
        this.maxWait = maxWait;
        this.validationQuery = validationQuery;
        this.testOnBorrow = testOnBorrow;
        this.spy = spy;
    }

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
