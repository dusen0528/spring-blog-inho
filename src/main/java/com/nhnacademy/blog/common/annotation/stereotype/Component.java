package com.nhnacademy.blog.common.annotation.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO#2-1 @Component
 * 모든 Stereotype의 기본이 되는 Annotation
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Component {
    String value() default "";
}