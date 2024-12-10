package com.nhnacademy.blog.common.reflection;

import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.annotation.Qualifier;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.exception.BeanNotFoundException;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;

public class ReflectionUtils {
    private ReflectionUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * private field에 reflection 을 이용해서 값 할당
     * @param target
     * @param fieldName
     * @param value
     */
    public static void setField(Object target, String fieldName, Object value) {
        Field field = null;
        try {
            field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * packageName 경로를 기준으로 targetClass를 구현한 class를 스켄후, InitOrder annotation 기반으로 정렬해서 List로 반환
     * @param packageName 스켄한 package명
     * @param targetClass 인터페이스
     * @return
     * @param <T>
     */
    public static <T>List<ClassWrapper<T>>  classScan(String packageName, Class<T> targetClass) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends T>> classes = reflections.getSubTypesOf(targetClass);
        List<ClassWrapper<T>> classWrappers = new ArrayList<>();

        for(Class<? extends T> clazz : classes){
            InitOrder initOrder = clazz.getAnnotation(InitOrder.class);
            int order = Objects.nonNull(initOrder) ? initOrder.value() : 1;
            try {
                classWrappers.add(new ClassWrapper(order,clazz));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //@InitOrder value 기준으로 내림차순 정렬
        Collections.sort(classWrappers, (o1, o2)->o1.getOrder()-o2.getOrder());
        return classWrappers;
    }

    /**
     * packageName 기준으로 class에 annotatedClass가 정의되어 있는 class를 스켄 합니다.
     * @param packageName
     * @param annotatedClass
     * @return
     */
    public static List<ClassWrapper> classScanByAnnotated(String packageName, Class<? extends Annotation> annotatedClass) {

        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> annotatedClasses = reflections.get(Scanners.TypesAnnotated.with(annotatedClass).asClass());
        List<ClassWrapper> classWrappers = new ArrayList<>();

        for(Class<?> clazz : annotatedClasses){
            InitOrder initOrder = clazz.getAnnotation(InitOrder.class);
            int order = Objects.nonNull(initOrder) ? initOrder.value() : 1;
            try {
                classWrappers.add(new ClassWrapper(order,clazz));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //@InitOrder value 기준으로 내림차순 정렬
        Collections.sort(classWrappers, (o1, o2)->o1.getOrder()-o2.getOrder());

        return classWrappers;
    }

    /**
     * clazz에 해당되는 처음 발견되는 Constructor를 반환 합니다.
     * @param clazz
     * @return
     */
    public static Constructor findFirstConstructor(Class<?> clazz) {
        Constructor[] constructors = clazz.getConstructors();

        if(constructors.length > 0){
            return constructors[0];
        }
        throw new RuntimeException("Constructor not found");
    }

    /**
     * 생성자의 parameter에 해당되는 객체를 @Qulifier annotation에 정의된 beanName에 의해서 조회후 Object[] array행태로 반환
     * @param context
     * @param constructor
     * @return Object[]
     */
    public static Object[] getParameterFromContext(Context context, Constructor constructor){
        Object[] parameters = null;

        if(constructor.getParameterTypes().length == 0 ){
            return new Object[0];
        }

        parameters = new Object[constructor.getParameterTypes().length];

        for (int i=0; i<constructor.getParameterCount(); i++) {
            Parameter parameter = constructor.getParameters()[i];

            //@qulifier annotation에 정의된 beanName을 구합니다.
            Qualifier qualifier = parameter.getAnnotation(Qualifier.class);

            if(Objects.isNull(qualifier)) {
                throw new RuntimeException(String.format("%s, missing @Qualifier annotation in Constructor:%s", constructor.getName(), parameter.getName()));
            }

            String beanName = qualifier.value();

            //context에서 @Qulifier(value="{beanName}")에 해당된 객체를 얻습니다.
            Object bean = context.getBean(beanName);

            if(Objects.isNull(bean)) {
                throw new BeanNotFoundException(String.format("%s, bean not found", beanName));
            }

            //해당 객체를 parameter로 할당 합니다.
            parameters[i] = bean;
        }
        return parameters;
    }

}
