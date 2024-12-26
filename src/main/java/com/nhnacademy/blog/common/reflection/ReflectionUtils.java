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

        Field field = null;
        try {
            field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

}
