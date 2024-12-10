package com.nhnacademy.blog.common.reflection.exception;

public class ReflectionException extends RuntimeException {

    public ReflectionException(Throwable throwable) {
        super(throwable);
    }
    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(String message,Throwable throwable) {
        super(message,throwable);
    }
}
