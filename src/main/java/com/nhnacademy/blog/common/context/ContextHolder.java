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

//Context에 접근할 수 있도록 ContextHolder를 Singleton 구현 합니다.
public class ContextHolder {
    private static final Context context = new ApplicationContext();

    private ContextHolder(){
        throw new IllegalStateException("ContextHolder should not be instantiated");
    }

    public static synchronized ApplicationContext getApplicationContext() {
        return (ApplicationContext) context;
    }
}