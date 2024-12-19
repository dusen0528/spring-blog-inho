package com.nhnacademy.blog.common.db;

import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
@Slf4j
public class DbUtils {
    private DbUtils(){
        throw new IllegalStateException("Utility class");
    }

    public static synchronized DataSource getDataSource(){
        Context context = ContextHolder.getApplicationContext();
        BlogDataSource blogDataSource = (BlogDataSource) context.getBean(BlogDataSource.BEAN_NAME);
        return blogDataSource.getDataSource();
    }
}