package com.nhnacademy.blog.common.init.impl;

import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.annotation.stereotype.Component;
import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.annotation.stereotype.Service;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.init.Initializeable;
import com.nhnacademy.blog.common.reflection.ClassWrapper;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.reflection.exception.ReflectionException;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.*;

@Slf4j
@InitOrder(value = 4)
@SuppressWarnings("java:S3740")
public class InitComponent implements Initializeable {

    @Override
    public void initialize(Context context) {
        //@Component annotation을 기준으로 class를 scan 합니다.
        List<ClassWrapper> classWrappers =  ReflectionUtils.classScanByAnnotated("com.nhnacademy.blog", Component.class);

        //1순위
        List<Class<?>> componentList = new ArrayList<>();
        //2순위
        List<Class<?>> serviceList = new ArrayList<>();
        //3순위
        List<Class<?>> repositoryList = new ArrayList<>();

        for (ClassWrapper<Component> classWrapper : classWrappers) {
            log.debug("find stereotype class: {}", classWrapper.getClazz().getSimpleName());

            if(classWrapper.getClazz().isInterface()){
                continue;
            }

            Component component = classWrapper.getClazz().getAnnotation(Component.class);
            Repository repository = classWrapper.getClazz().getDeclaredAnnotation(Repository.class);
            Service service = classWrapper.getClazz().getDeclaredAnnotation(Service.class);

            if(Objects.nonNull(component)){
                componentList.add(classWrapper.getClazz());
            }else if(Objects.nonNull(repository)){
                repositoryList.add(classWrapper.getClazz());
            }else if(Objects.nonNull(service)){
                serviceList.add(classWrapper.getClazz());
            }
        }//end for

        for (Class<?> aClass : componentList) {
            Component component = aClass.getDeclaredAnnotation(Component.class);
            String beanName = component.value().isBlank() ? classNameToBeanName(aClass) : component.value();
            createInstance(context,aClass,beanName);
        }

        for(Class<?> aClass : repositoryList) {
            Repository repository = aClass.getDeclaredAnnotation(Repository.class);
            String beanName = repository.value().isBlank() ? classNameToBeanName(aClass) : repository.value();
            createInstance(context,aClass,beanName);
        }

        for (Class<?> aClass : serviceList) {
            Service service = aClass.getDeclaredAnnotation(Service.class);
            String beanName = service.value().isBlank() ? classNameToBeanName(aClass) : service.value();
            createInstance(context,aClass,beanName);
        }
    }//end method

    /**
     * className -> beanName으로 변경
     * ex)PasswordEncoder -> passwordEncoder
     * @param clazz
     * @return beanName
     */
    public static String classNameToBeanName(Class<?> clazz) {
        String className = clazz.getSimpleName();
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }


    private void createInstance(Context context, Class targetClass, String beanName) {

        //해당 targetClass 생성자 목록 구하기.
        Constructor[] constructors = targetClass.getConstructors();
        log.debug("{} - constructors-length:{}", targetClass.getSimpleName() ,constructors.length);

        //객체 생성에 사용할 Constructor를 구합니다.
        Constructor constructor = ReflectionUtils.findFirstConstructor(targetClass);

        //constructor에 해당되는 parameter 순서대로 Object[] 배열형태로 반환 합니다. parameter에 해당되는 객체는 ApplicationContext에서 등록되어 있는 bean들을 할당 합니다.
        Object[] parameters = ReflectionUtils.getParameterFromContext(context,constructor);

        try {
            //객체를 생성 후 context에 등록 합니다.
            Object instance = constructor.newInstance(parameters);
            context.registerBean(beanName,instance);
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }
}
