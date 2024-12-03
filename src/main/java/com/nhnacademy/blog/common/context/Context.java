package com.nhnacademy.blog.common.context;

public interface Context {
    //Object를 등록합니다.
    void registerBean(String name, Object object);
    //Object를 삭제합니다.
    void removeBean(String name);
    //Object를 얻습니다.
    Object getBean(String name);
}