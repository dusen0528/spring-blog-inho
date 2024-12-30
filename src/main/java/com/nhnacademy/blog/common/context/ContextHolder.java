/*
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * + Copyright 2024. NHN Academy Corp. All rights reserved.
 * + * While every precaution has been taken in the preparation of this resource,  assumes no
 * + responsibility for errors or omissions, or for damages resulting from the use of the information
 * + contained herein
 * + No part of this resource may be reproduced, stored in a retrieval system, or transmitted, in any
 * + form or by any means, electronic, mechanical, photocopying, recording, or otherwise, without the
 * + prior written permission.
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */

package com.nhnacademy.blog.common.context;

import com.nhnacademy.blog.common.config.ApplicationConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * ContextHolder 클래스는 애플리케이션 전역에서 사용할 수 있는
 * Spring ApplicationContext를 관리합니다.
 *
 * - 애플리케이션 컨텍스트는 AnnotationConfigApplicationContext를 기반으로 생성됩니다.
 * - ApplicationConfig.class를 설정으로 사용하여 초기화됩니다.
 * - 이 클래스는 인스턴스화(객체 생성)를 허용하지 않으며, 전역적으로 하나의 컨텍스트만 사용되도록 설계되었습니다.
 */
public class ContextHolder {

    /**
     * - AnnotationConfigApplicationContext를 사용하여 애플리케이션 컨텍스트를 생성합니다.
     * - ApplicationConfig.class를 설정으로 사용합니다.
     * - 초기화 시점에 클래스가 로드될 때 애플리케이션 컨텍스트가 생성됩니다.
     */
    private static final ApplicationContext context;
    static {
        //ApplicationContext 생성
        context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
    }

    /**
     * ContextHolder 클래스의 기본 생성자.
     * - 이 클래스는 인스턴스화가 금지되어 있습니다.
     * - new ContextHolder()를 호출하려고 하면 IllegalStateException 예외를 발생시킵니다.
     */
    private ContextHolder() {
        throw new IllegalStateException("ContextHolder should not be instantiated");
    }

    /**
     * 애플리케이션 컨텍스트를 반환합니다.
     * - 동기화를 통해 안전하게 컨텍스트를 반환합니다.
     *
     * @return ApplicationContext 애플리케이션 전역 컨텍스트
     */
    public static synchronized ApplicationContext getApplicationContext() {
        return context;
    }
}
