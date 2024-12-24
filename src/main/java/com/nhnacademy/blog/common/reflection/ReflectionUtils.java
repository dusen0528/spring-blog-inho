package com.nhnacademy.blog.common.reflection;
import com.nhnacademy.blog.common.reflection.exception.ReflectionException;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

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
     * packageName 기준으로 class에 annotatedClass가 정의되어 있는 class를 스켄 합니다.
     * @param packageName
     * @param annotatedClass
     * @return
     */

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


}
