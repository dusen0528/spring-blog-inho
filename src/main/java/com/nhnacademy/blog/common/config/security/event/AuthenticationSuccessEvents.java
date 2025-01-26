package com.nhnacademy.blog.common.config.security.event;

import com.nhnacademy.blog.common.config.security.userdetail.MemberDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * TODO#8 - 인증이 성공했을 때 시점에 호출되는 이벤트 리스터 입니다.
 * - 스프링은 ApplicationListener<이벤트.class>지정하면 해당 이벤트가 발생되는 시점에 호출 합니다.
 * - AuthenticationSuccessEvent <-- 인증이 성공했을 때 발생되는 이벤트 입니다.
 */

@Slf4j
@Component
public class AuthenticationSuccessEvents implements ApplicationListener<AuthenticationSuccessEvent>{

    @Override
    public void onApplicationEvent(org.springframework.security.authentication.event.AuthenticationSuccessEvent event) {
        /**
         * TODO#8-2  event.getAuthentication() 게체로 부터 principal 객체를 획득 합니다.
         *  - principal 객체는 MemberDetails 객체로 케스팅 합니다.
         */

        MemberDetails memberDetails = (MemberDetails) event.getAuthentication().getPrincipal();

        //TODO#8-2 memberDetails객체의 ereasePassword() 메소드를 호출하여 인증이 종료된 후
        //- 비밀번호 노출을 최소화 하기 위해서 password를 초기화 합니다.
        //- 로그인 성공후 http://localhost:8080/index.do  접속하여 비밀번호 실행 여부에 따른 어떤 차의가 있는지 비교합니다.
        memberDetails.ereasePassword();
        log.debug("onApplicationEvent-memberDetails: {}", memberDetails);

    }

}
