package com.nhnacademy.blog.common.init.impl;

import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.init.Initializeable;
import com.nhnacademy.blog.common.reflection.ClassWrapper;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
@InitOrder(value = 3)
public class InitRepository implements Initializeable {
    private Context context;

    @Override
    public void initialize(Context context) {
        this.context = context;
        List<ClassWrapper> classWrappers =  ReflectionUtils.classScanByAnnotated("com.nhnacademy.blog", Repository.class);

        for (ClassWrapper<Repository> classWrapper : classWrappers) {
            log.debug("Initializing Repository: {}", classWrapper.getClazz().getSimpleName());
            Object instance = null;

            try {
                instance = classWrapper.getClazz().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Repository repository = instance.getClass().getAnnotation(Repository.class);

            if(Objects.nonNull(repository)) {
                context.registerBean(repository.name(), instance);
            }
        }
    }
}