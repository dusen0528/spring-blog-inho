package com.nhnacademy.blog.common.db;

import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

/**
 * BlogDataSource는 context.getBean() 메서드를 통해 직접 접근할 수 있지만, DbUtils를 통해 프로젝트 어디에서든지
 * BlogDataSource에 접근할 수 있도록 지원하는 유틸리티 클래스입니다.
 */
@Slf4j
public class DbUtils {
    private DbUtils(){
        //TODO#6-9 DbUtils 객체를 생성하려고 시도한다면 IllegalStateException이 발생 합니다.
        throw new IllegalStateException("Utility class");
    }

    public static synchronized DataSource getDataSource(){
        /*TODO#6-10 context로부터 BlogDataSource의 Bean인 dataSource를 반환합니다.
            - dataSource는 dbcp2 기반의 Connection pool입니다.
            - context에 접근하기 위해서는 ContextHolder.getApplicationContext()를 통해서 접근할 수 있습니다.
            - context로부터 BlogDataSource Bean을 얻기 위해서는 context.getBean("beanName")을 통해서 얻을 수 있습니다.
         */

        Context context = ContextHolder.getApplicationContext();
        BlogDataSource blogDataSource = (BlogDataSource) context.getBean(BlogDataSource.BEAN_NAME);
        return blogDataSource.getDataSource();
    }
}