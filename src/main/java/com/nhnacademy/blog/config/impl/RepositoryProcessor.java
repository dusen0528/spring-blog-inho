package com.nhnacademy.blog.config.impl;

import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.repository.JdbcRepository;
import com.nhnacademy.blog.config.StartProcessor;

import java.util.*;

public class RepositoryProcessor implements StartProcessor {

    @Override
    public void process() {
        Context context = ContextHolder.getApplicationContext();

        List<JdbcRepository> jdbcRepositories = getRepositories();
        for(JdbcRepository jdbcRepository : jdbcRepositories) {
            context.registerBean(jdbcRepository.getBeanName(),jdbcRepository);
        }
    }

    private List<JdbcRepository> getRepositories() {
        ServiceLoader<JdbcRepository> loader = ServiceLoader.load(JdbcRepository.class);
        Iterator<JdbcRepository> iterator = loader.iterator();
        List<JdbcRepository> jdbcRepositories = new ArrayList<>();

        while (iterator.hasNext()) {
            JdbcRepository jdbcRepository = iterator.next();
            jdbcRepositories.add(jdbcRepository);
        }
        return jdbcRepositories;
    }

}
