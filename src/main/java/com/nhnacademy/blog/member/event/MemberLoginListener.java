package com.nhnacademy.blog.member.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * TODO#2 - MemberLoginListener는 회원 로그인 시 발생한 MemberLoginEvent를 처리하는 리스너입니다.
 * 로그인 이벤트가 발생하면 사용자의 이메일을 로그로 출력하거나, 이메일 발송 등의 후속 작업을 처리할 수 있습니다.
 */

@Slf4j
//TODO#2-1 @Component 를 사용하여 Bean으로 등록 합니다.
@Component
public class MemberLoginListener {

    //TODO#2-2 @EventListener annotation을 선언 합니다. classes 에는 Event Target class(MemberLoginEvent.class)를 지정 합니다.
    @EventListener(classes = MemberLoginEvent.class)
    public void handleMemberLoginEvent(MemberLoginEvent event) {
        //이메일 발송 대신 로그로 대체
        log.debug("로그인알림 이메일:{} 발송",event.getEmail());
    }
}
