package com.nhnacademy.blog.common.reflection;

import java.util.Objects;

public class ClassWrapper<T> {

    private final int order;
    private final Class<T> clazz;

    public ClassWrapper(int order, Class<T> clazz) {
        if(Objects.isNull(clazz)) {
            throw new IllegalArgumentException("instance is null!");
        }
        this.order = order;
        this.clazz = clazz;
    }

    public int getOrder() {
        return order;
    }

    public Class<T> getClazz() {
        return clazz;
    }

}
