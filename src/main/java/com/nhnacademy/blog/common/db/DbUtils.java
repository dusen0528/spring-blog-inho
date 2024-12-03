package com.nhnacademy.blog.common.db;

import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;

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