package com.nhnacademy.blog.common.context;

import com.nhnacademy.blog.common.context.exception.BeanNotFoundException;
import com.nhnacademy.blog.common.init.Initializeable;
import com.nhnacademy.blog.common.reflection.ClassWrapper;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.reflection.exception.ReflectionException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ApplicationContext  implements Context{
    ConcurrentMap<String, Object> beanMap;

    public ApplicationContext() {
        //map 초기화
        this.beanMap = new ConcurrentHashMap<>();
        initialize();
    }

    private void initialize(){

        log.debug("Initializeable based, initializing application context");

        //Initializeable 구현 class를 scan 한다.
        List<ClassWrapper<Initializeable>> classWrappers = ReflectionUtils.classScan("com.nhnacademy.blog", Initializeable.class);

        //initialize method 호출
        for(ClassWrapper<Initializeable> classWrapper : classWrappers){
            log.debug("registering bean : {}", classWrapper.getClazz().getSimpleName());

            try {
                Initializeable initializeable = classWrapper.getClazz().getDeclaredConstructor().newInstance();
                initializeable.initialize(this);
            } catch (Exception e) {
                throw new ReflectionException(e);
            }
        }

        log.debug("size:{}", classWrappers.size());
    }

    @Override
    public void registerBean(String name, Object object) {
        objectNameCheck(name);
        beanMap.put(name,object);
    }

    @Override
    public void removeBean(String name) {
        objectNameCheck(name);
        beanMap.remove(name);
    }

    @Override
    public Object getBean(String name) {
        objectNameCheck(name);
        Object object =  beanMap.get(name);

        //object가 존재하지 않는다면 BeanNotFoundException 예외가 발생합니다.
        if(Objects.isNull(object)) {
            throw new BeanNotFoundException(name);
        }
        return object;
    }

    private void objectNameCheck(String name){
        if(Objects.isNull(name) || name.isEmpty()){
            throw new IllegalArgumentException(name);
        }
    }

}