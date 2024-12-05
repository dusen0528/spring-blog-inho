package com.nhnacademy.blog.common.init.impl;

import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.annotation.stereotype.Component;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.init.Initializeable;
import com.nhnacademy.blog.common.reflection.ClassWrapper;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.List;

@Slf4j
@InitOrder(value = 4)
public class InitComponent implements Initializeable {

    @Override
    public void initialize(Context context) {
        //@Component annotation을 기준으로 class를 scan 합니다.
        List<ClassWrapper> classWrappers =  ReflectionUtils.classScanByAnnotated("com.nhnacademy.blog", Component.class);

        for (ClassWrapper<Component> classWrapper : classWrappers) {
            log.debug("Initializing Component: {}", classWrapper.getClazz().getSimpleName());

            Class targetClass =  classWrapper.getClazz();
            //해당 class의 생성자 목록 구하기.
            Constructor[] constructors = targetClass.getConstructors();
            log.debug("constructors-length:{}",constructors.length);

            Component component = (Component) targetClass.getDeclaredAnnotation(Component.class);

            //객체 생성에 사용할 Constructor를 구합니다.
            Constructor constructor = ReflectionUtils.findFirstConstructor(targetClass);

            //constructor에 해당되는 parameter 순서대로 Object[] 배열형태로 반환 합니다. parameter에 해당되는 객체는 ApplicationContext에서 등록되어 있는 bean들을 할당 합니다.
            Object[] parameters = ReflectionUtils.getParameterFromContext(context,constructor);

            try {
                //객체를 생성 후 context에 등록 합니다.
                Object instance = constructor.newInstance(parameters);
                context.registerBean(component.name(),instance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }//end for
    }//end method
}
