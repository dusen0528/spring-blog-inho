package com.nhnacademy.blog.member.dto;


import java.time.LocalDateTime;

/**
 * TODO#10 - dto는 interface 타입만 사용할 수 있습니다.
 *  - class는 사용할 수 업습니다.
 *  - interface dto 형태의 프로젝션은 제한적인 형태로 사용할 수 있습니다.
 *  - 메소드를 구현한다던가, 복잡한 처리의 어려움.(단점)
 *  - 이런 부분들을 나중에 QueryDSL을 통해서 해결할 수 있습니다.
 *  - 우선 interface 기반으로 dto projection 할 수 있다 정도로 정리 합시다.
 */
public interface MemberResponse {
    Long getMbNo();
    String getMbEmail();
    String getMbName();
    String getMbMobile();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    LocalDateTime getWithdrawalAt();
}
