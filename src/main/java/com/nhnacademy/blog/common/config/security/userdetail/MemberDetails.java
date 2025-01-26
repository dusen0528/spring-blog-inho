package com.nhnacademy.blog.common.config.security.userdetail;

import com.nhnacademy.blog.member.dto.MemberResponse;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 *
 * TODO#5 - 이 클래스는 Spring Security의 UserDetails 인터페이스를 구현하여 인증과 권한 부여에 필요한 사용자 정보를 제공합니다.
 * MemberResponse와 MemberGrantedAuthority를 기반으로 인증 정보와 권한을 관리하며, 탈퇴 여부 등을 고려하여 계정 상태를 확인합니다.
 */

@ToString
@RequiredArgsConstructor
public class MemberDetails implements UserDetails {

    private final MemberResponse memberResponse;  // 사용자의 회원 정보
    private final List<MemberGrantedAuthority> memberGrantedAuthorities;  // 사용자의 권한 목록
    private String password;  // 사용자의 비밀번호

    /**
     * TODO#5-1 생성자 초기화
     * Constructor to initialize MemberDetails with the member's information, authorities, and password.
     * @param memberResponse 회원 정보 객체
     * @param memberGrantedAuthorities 권한 리스트
     * @param password 비밀번호
     */
    public MemberDetails(MemberResponse memberResponse, List<MemberGrantedAuthority> memberGrantedAuthorities, String password) {
        this.memberResponse = memberResponse;
        this.memberGrantedAuthorities = memberGrantedAuthorities;
        this.password = password;
    }

    /**
     * TODO#5-2 반환된 권한 목록을 통해 사용자가 가진 권한들을 가져옵니다.
     * @return 사용자의 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return memberGrantedAuthorities;
    }

    /**
     * TODO#5-3 비밀번호를 반환합니다.
     * - AuthenticationProvider는 password를 기반으로 인증 여부를 결정 합니다.
     * @return 사용자의 비밀번호
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * TODO#5-4 사용자의 이메일을 반환하여 사용자명을 나타냅니다.
     * @return 사용자의 이메일
     */
    @Override
    public String getUsername() {
        return memberResponse.getMbEmail();  // 이메일을 사용자명으로 사용
    }

    /**
     * TODO#5-5 계정이 만료되지 않았는지 확인합니다.
     * 만약 사용자가 탈퇴했다면 계정은 만료된 것으로 간주됩니다.
     * @return true: 계정 만료 안됨, false: 계정 만료됨
     */
    @Override
    public boolean isAccountNonExpired() {
        return Objects.isNull(memberResponse.getWithdrawalAt());  // 탈퇴일이 null이면 만료되지 않음
    }

    /**
     * TODO#5-6 계정이 잠겨 있지 않은지 확인합니다.
     * 탈퇴일이 있으면 계정이 잠긴 것으로 간주됩니다.
     * @return true: 계정 잠금 안됨, false: 계정 잠금됨
     */
    @Override
    public boolean isAccountNonLocked() {
        return Objects.isNull(memberResponse.getWithdrawalAt());  // 탈퇴일이 null이면 잠기지 않음
    }

    /**
     * TODO#5-7 비밀번호가 만료되지 않았는지 확인합니다.
     * @return true: 비밀번호 만료 안됨, false: 비밀번호 만료됨
     * - 비밀번호 만료에 대한 처리가 설계쌍 구현되어 있지 않음
     * - 일괄적으로 true 반환합니다.(만료되지 않음 설정)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * TODO#5-8 사용자가 활성화 되어 있는지(즉, 탈퇴하지 않았는지) 확인합니다.
     * @return true: 사용자가 활성화됨, false: 사용자가 비활성화됨
     */
    @Override
    public boolean isEnabled() {
        return Objects.isNull(memberResponse.getWithdrawalAt());  // 탈퇴일이 null이면 활성화됨
    }

    /**
     *  TODO#5-9 `MemberResponse` 객체를 반환합니다.
     * @return `MemberResponse` 객체
     */
    public MemberResponse getMemberResponse() {
        return memberResponse;
    }

    /**
     * TODO#5-10 사용자의 비밀번호를 지웁니다.
     * 보안을 위해 인증이 완료된 후 비밀번호를 지울 때 사용될 수 있습니다.
     * - 인증이 완료된 시점에 Secrutity에서 제공하는 AuthenticationSuccessEvent를 기반으로 처리할 예정
     */
    public void ereasePassword() {
        this.password = "";  // 비밀번호를 빈 문자열로 설정하여 보안을 강화합니다.
    }

    /**
     * TODO#5-11 객체가 동일한지 비교하는 메서드입니다. 두 `MemberDetails` 객체가 같은지 확인합니다.
     * @param o 비교 대상 객체
     * @return true: 동일한 객체, false: 다른 객체
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MemberDetails that = (MemberDetails) o;
        return Objects.equals(memberResponse, that.memberResponse) &&
                Objects.equals(memberGrantedAuthorities, that.memberGrantedAuthorities);
    }

    /**
     * TODO#5-12 객체의 해시코드를 반환하는 메서드입니다. 두 `MemberDetails` 객체의 해시코드가 동일한지 확인합니다.
     * @return 해시코드 값
     */
    @Override
    public int hashCode() {
        return Objects.hash(memberResponse, memberGrantedAuthorities);
    }

    /**
     * TODO#5-13 UserDetails 객체는 반드시 equals & hashCode를 구현 합니다.(필수)
     * 세션 관리 및 캐시 관리에서 정확한 사용자 정보 비교를 보장하기 위해.
     * 인증과 권한 비교에서 객체가 일관되게 처리될 수 있도록 하기 위해.
     * 성능 최적화와 해시 기반 컬렉션의 효율적인 관리.
     * 사용자 정보 변경 시 일관성 유지를 위해.
     */

}
