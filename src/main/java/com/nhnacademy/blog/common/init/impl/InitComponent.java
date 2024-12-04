package com.nhnacademy.blog.common.init.impl;

import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.annotation.Qualifier;
import com.nhnacademy.blog.common.annotation.stereotype.Component;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.exception.BeanNotFoundException;
import com.nhnacademy.blog.common.init.Initializeable;
import com.nhnacademy.blog.common.reflection.ClassWrapper;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Objects;

@Slf4j
@InitOrder(value = 4)
public class InitComponent implements Initializeable {

    @Override
    public void initialize(Context context) {

        List<ClassWrapper> classWrappers =  ReflectionUtils.classScanByAnnotated("com.nhnacademy.blog", Component.class);

        for (ClassWrapper<Component> classWrapper : classWrappers) {
            log.debug("Initializing Component: {}", classWrapper.getClazz().getSimpleName());

            Class targetClass =  classWrapper.getClazz();
            Constructor[] constructors = targetClass.getConstructors();

            log.debug("constructors-length:{}",constructors.length);

            Component component = (Component) targetClass.getDeclaredAnnotation(Component.class);

            for (Constructor constructor : constructors) {

                Object[] parameters = new Object[constructor.getParameterCount()];

                for (int i=0; i<constructor.getParameterCount(); i++) {
                    Parameter parameter = constructor.getParameters()[i];

                    Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
                    if(Objects.isNull(qualifier)) {
                        throw new RuntimeException(String.format("%s, missing @Qualifier annotation in Constructor", targetClass.getSimpleName()));
                    }
                    String beanName = qualifier.value();
                    Object bean = context.getBean(beanName);
                    if(Objects.isNull(bean)) {
                        throw new BeanNotFoundException(String.format("%s, bean not found", beanName));
                    }
                    parameters[i] = bean;
                }

                try {
                    Object instance = constructor.newInstance(parameters);
                    context.registerBean(component.name(),instance);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                break;
            }
        }
    }
}
