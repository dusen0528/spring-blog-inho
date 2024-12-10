package com.nhnacademy.blog.common.db.exception;

public class DatabaseException extends RuntimeException {
    public DatabaseException(Throwable throwable) {
        super("SQLException",throwable);
    }
}
