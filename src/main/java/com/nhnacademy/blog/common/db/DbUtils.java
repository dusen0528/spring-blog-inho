package com.nhnacademy.blog.common.db;

import com.nhnacademy.blog.common.context.ContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;

/**
 * BlogDataSource는 context.getBean() 메서드를 통해 직접 접근할 수 있지만, DbUtils를 통해 프로젝트 어디에서든지
 * BlogDataSource에 접근할 수 있도록 지원하는 유틸리티 클래스입니다.
 */
@Slf4j
public class DbUtils {
    private DbUtils(){
        throw new IllegalStateException("Utility class");
    }

    public static synchronized DataSource getDataSource(){
       ApplicationContext context = ContextHolder.getApplicationContext();
       DataSource dataSource = (DataSource) context.getBean("dataSource");
        return dataSource;
    }

}