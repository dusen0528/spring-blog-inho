package com.nhnacademy.blog.common.init;

import com.nhnacademy.blog.common.context.Context;

/**
 * application이 시작될 때 initialize()를 호출해서 초기화 합니다.
 */
public interface Initializeable {
    void initialize(Context context);
}
