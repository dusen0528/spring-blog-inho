package com.nhnacademy.blog.config.context;

import com.nhnacademy.blog.config.context.exception.BeanNotFoundException;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ApplicationContext  implements Context{
    ConcurrentMap<String, Object> beanMap;

    public ApplicationContext() {
        this.beanMap = new ConcurrentHashMap<>();
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
        if(Objects.isNull(name) || name.length()==0){
            throw new IllegalArgumentException(name);
        }
    }
}