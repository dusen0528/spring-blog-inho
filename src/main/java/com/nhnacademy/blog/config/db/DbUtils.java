package com.nhnacademy.blog.config.db;

import com.nhnacademy.blog.config.context.Context;
import com.nhnacademy.blog.config.context.ContextHolder;

import javax.sql.DataSource;

public class DbUtils {
    public DbUtils(){
        throw new IllegalStateException("Utility class");
    }

    public static DataSource getDataSource(){
        Context context = ContextHolder.getApplicationContext();
        DataSource dataSource = (DataSource) context.getBean(InitDataSource.BEAN_NAME);
        return dataSource;
    }
}