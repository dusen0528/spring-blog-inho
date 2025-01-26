package com.nhnacademy.blog.common.security.userdetail;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;


/**
 * GrantedAuthority는 Spring Security에서 사용자의 권한을 추상화하는 역할을 합니다.
 * Spring Security는 인증된 사용자(UserDetails)에게 권한을 부여하는데, 이 권한을 GrantedAuthority 객체로 나타냅니다.
 * 권한은 보통 ROLE_USER, ROLE_ADMIN와 같은 문자열로 표현됩니다.
 * GrantedAuthority 인터페이스는 권한을 검증하거나 관리하는 데 필요한 메서드를 제공합니다.
 * - blog appplication 권한 : ROLE_OWNER ( 블로그 소유자 ) , ROLE_MEMBER(블로그 회원), ROLE_ADMIN(시스템 관리자)
 *
 * - 블로그는에 따라서 권한이 다르게 설정될 수 있음으로 권한에 blogId, blogFid를 추가 했습니다.
 * - 즉 blog에 따른 권한식별을 위해서 추가 했습니다.
 */

@ToString
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class MemberGrantedAuthority implements GrantedAuthority {

    private final long blogId; //블로그 아이디
    private final String blogFid; //블로그 대표이름 ex) http://localhost:8080/marco/post/1 , marco <-- 블로그 식별자
    private final String roleId; //권한 ex) ROLE_OWNER
    private final String roleName; //권한 이름, ex)블로그 소유자

    @Override
    public String getAuthority() {
        //권한을 반환 합니다.
        return this.roleId;
    }

}