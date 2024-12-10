package com.nhnacademy.blog.common.db;

import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;

import javax.sql.DataSource;

public class DbUtils {
    private DbUtils(){
        throw new IllegalStateException("Utility class");
    }

    public static DataSource getDataSource(){
        Context context = ContextHolder.getApplicationContext();
        BlogDataSource blogDataSource = (BlogDataSource) context.getBean(BlogDataSource.BEAN_NAME);
        return blogDataSource.getDataSource();
    }
}