package com.nhnacademy.blog.common.db;

public class DbProperties {
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

    public DbProperties(String url, String username, String password, int initialSize, int maxTotal, int maxIdle, int minIdle, int maxWait, String validationQuery, boolean testOnBorrow) {
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
    }

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
                '}';
    }
}
