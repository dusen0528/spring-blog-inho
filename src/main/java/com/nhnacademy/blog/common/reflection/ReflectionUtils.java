package com.nhnacademy.blog.common.reflection;

import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.annotation.Qualifier;
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
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
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

}
