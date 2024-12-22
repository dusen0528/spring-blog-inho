package com.nhnacademy.blog.common.annotation.stereotype;

/**
 * TODO#2-3 비지니스 로직을 구현하는 서비스 클레스에 사용됩니다.
 * 주로 서비스 레이어에 위치하며, 애플리케이션의 핵심 비즈니스 로직을 처리하는 메서드를 포함 합니다.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Component
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Service {
    String value() default "";
}