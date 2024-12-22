package com.nhnacademy.blog.common.reflection;

import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.annotation.Qualifier;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.exception.BeanNotFoundException;
import com.nhnacademy.blog.common.reflection.exception.ReflectionException;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
@Slf4j
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
    @SuppressWarnings("java:S3011")
    public static void setField(Object target, String fieldName, Object value) {
        /*TODO#5-12 객체의 특정 field에 값을 강제로 할당 합니다.*/

        Field field = null;
        try {
            field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * packageName 경로를 기준으로 targetClass를 구현한 class를 스켄후, InitOrder annotation 기반으로 정렬해서 List로 반환
     * @param packageName 스켄한 package명
     * @param targetClass 인터페이스
     * @return
     * @param <T>
     */
    @SuppressWarnings("java:S3740")
    public static <T>List<ClassWrapper<T>>  classScan(String packageName, Class<T> targetClass) {
        //TODO#5-13 classScan() 구현합니다.

        //Reflections객체를 packageName을 사용해서 초기화 합니다.
        Reflections reflections = new Reflections(packageName);
        //targetClass를 구현한 구현체를 Scan 합니다.
        Set<Class<? extends T>> classes = reflections.getSubTypesOf(targetClass);

        List<ClassWrapper<T>> classWrappers = new ArrayList<>();
        for(Class<? extends T> clazz : classes){
            //스캔된 class는 ClassWrapper 객체에 담아서 classWrappers 리스트에 등록 합니다.
            // new ClassWrapper(@InitOrder에 설정된 우선순위, class) 초기화 합니다.
            // @InitOrder 존재하지 않다면 1로 초기화 합니다.

            InitOrder initOrder = clazz.getAnnotation(InitOrder.class);
            int order = Objects.nonNull(initOrder) ? initOrder.value() : 1;
            try {
                classWrappers.add(new ClassWrapper(order,clazz));
            } catch (Exception e) {
                throw new ReflectionException(e);
            }
        }

        //@InitOrder value 기준으로 classWrappers내의 class들을 내림차순 정렬합니다.
        Collections.sort(classWrappers, (o1, o2)->o1.getOrder()-o2.getOrder());
        return classWrappers;
    }

    /**
     * packageName 기준으로 class에 annotatedClass가 정의되어 있는 class를 스켄 합니다.
     * @param packageName
     * @param annotatedClass
     * @return
     */

    @SuppressWarnings("java:S3740")
    public static List<ClassWrapper> classScanByAnnotated(String packageName, Class<? extends Annotation> annotatedClass) {
        //TODO#5-14 classScanByAnnotated() 구현 합니다.

        //Reflections객체를 packageName을 사용해서 초기화 합니다.
        Reflections reflections = new Reflections(packageName);

        //annotatedClass이 달려있는 class를 스켄 합니다.
        Set<Class<?>> annotatedClasses = reflections.get(Scanners.TypesAnnotated.with(annotatedClass).asClass());

        List<ClassWrapper> classWrappers = new ArrayList<>();

        for(Class<?> clazz : annotatedClasses){

            //스캔된 class는 ClassWrapper 객체에 담아서 classWrappers 리스트에 등록 합니다.
            // new ClassWrapper(@InitOrder에 설정된 우선순위, class) 초기화 합니다.
            // @InitOrder 존재하지 않다면 1로 초기화 합니다.

            InitOrder initOrder = clazz.getAnnotation(InitOrder.class);
            int order = Objects.nonNull(initOrder) ? initOrder.value() : 1;
            try {
                classWrappers.add(new ClassWrapper(order,clazz));
            } catch (Exception e) {
                throw new ReflectionException(e);
            }
        }

        //@InitOrder value 기준으로 classWrappers내의 class들을 내림차순 정렬합니다.
        Collections.sort(classWrappers, (o1, o2)->o1.getOrder()-o2.getOrder());

        return classWrappers;
    }

    /**
     * clazz에 해당되는 처음 발견되는 Constructor를 반환 합니다.
     * @param clazz
     * @return
     */
    public static <T>Constructor<T> findFirstConstructor(Class<?> clazz) {
        //TODO#5-15 findFirstConstructor()를 구현 합니다.

        //clazz로 부터 생성자 리스트를 구합니다.
        Constructor<?>[] constructors = clazz.getConstructors();

        //constructors >0 면 처음 발견된 생성자를 반환 합니다.
        //constructors==0 이면  ReflectionException이 발생 합니다.
        if(constructors.length > 0){
            return (Constructor<T>) constructors[0];
        }
        throw new ReflectionException("Constructor not found");
    }

    /**
     * 생성자의 parameter에 해당되는 객체를 @Qulifier annotation에 정의된 beanName에 의해서 조회후 Object[] array행태로 반환
     * @param context
     * @param constructor
     * @return Object[]
     */
    @SuppressWarnings("java:S3740")
    public static Object[] getParameterFromContext(Context context, Constructor constructor){
        //TODO#5-16 constructor 생성자에 주입되는 parameter를 조회하고 해당 parameter에 해당되는 객체를 Context로 부터 Bean(객체)를 주입 받아서 Object[] 형태로 반환 합니다.

        Object[] parameters = null;

        //생성자의 파라미터 개수가 == 0이면 new Object[0]을 반환합니다.
        if(constructor.getParameterTypes().length == 0 ){
            return new Object[0];
        }
        //parameters 배열을 생성자의 파라미터 개수 만큼 배열 크기를 초기화 합니다.
        parameters = new Object[constructor.getParameterTypes().length];

        for (int i=0; i<constructor.getParameterCount(); i++) {

            /** 파라미터의 개수 만큼 순회 하면서 해당 파라미터에 해당되는 Bean을 찾아 Object[] 배열로 반환 합니다.
             다음과 같은 @Service가 있다고 예를 들어 봅시다.
             + BlogInfoServiceImpl 생성자에 다음과 같이 파라미터가 존재 합니다.
                - @Qualifier(JdbcBlogRepository.BEAN_NAME) BlogRepository blogRepository,
                - @Qualifier(JdbcBlogMemberMappingRepository.BEAN_NAME) BlogMemberMappingRepository blogMemberMappingRepository
             + BlogRepository blogRepository Application Context에서  @Qualifier(JdbcBlogRepository.BEAN_NAME)의 BeanName('blogInfoService')에 해당하는 객체를 찾아서 주입합니다.
             + 즉 Object[] 배열에 parameter의 순서에 맞춰 Context로부터 Bean(객체)를 할당 합니다.

             @Service(BlogInfoServiceImpl.BEAN_NAME)
             public class BlogInfoServiceImpl implements BlogInfoService {

                 public static final String BEAN_NAME = "blogInfoService";
                 private final BlogRepository blogRepository;
                 private final BlogMemberMappingRepository blogMemberMappingRepository;

                 public BlogInfoServiceImpl(
                 @Qualifier(JdbcBlogRepository.BEAN_NAME) BlogRepository blogRepository,
                 @Qualifier(JdbcBlogMemberMappingRepository.BEAN_NAME) BlogMemberMappingRepository blogMemberMappingRepository
                 ) {
                    this.blogRepository = blogRepository;
                    this.blogMemberMappingRepository = blogMemberMappingRepository;
                 }
                 //...
             }

             */
            Parameter parameter = constructor.getParameters()[i];

            //@qulifier annotation에 정의된 beanName을 구합니다.
            Qualifier qualifier = parameter.getAnnotation(Qualifier.class);

            // qualifier 가 존재하지 않다면 ReflectionException 발생 합니다.
            if(Objects.isNull(qualifier)) {
                throw new ReflectionException(String.format("%s, missing @Qualifier annotation in Constructor:%s", constructor.getName(), parameter.getName()));
            }
            //qualifier로 부터 beanName을 얻습니다.
            String beanName = qualifier.value();

            //context에서 @Qulifier(value="{beanName}")에 해당된 객체를 얻습니다.
            Object bean = context.getBean(beanName);

            //bean이 Context에 존재하지 않다면 BeanNotFoundException이 발생 합니다.
            if(Objects.isNull(bean)) {
                throw new BeanNotFoundException(String.format("%s, bean not found", beanName));
            }

            //bean(객체)을 순서에 맞춰 parameter[i]로 할당 합니다.
            parameters[i] = bean;
        }
        return parameters;
    }

}
