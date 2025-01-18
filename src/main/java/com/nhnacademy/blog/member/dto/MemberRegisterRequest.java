package com.nhnacademy.blog.member.dto;

import jakarta.validation.constraints.*;
import lombok.ToString;
import lombok.Value;

/**
 * TODO#3 - MemberRegisterRequest validation 될 수 있도록 annotation 기반으로 검증 조건을 설정합니다.
 */

@Value
@ToString
public class MemberRegisterRequest {
    /**
     * TODO#3-1 회원 이메일 검증
     *  - @Email,  message='유효한 이메일 주소를 입력해주세요.'
     *  - @NotBlank, message = '이메일은 필수 입력 항목입니다.'
     */
    // 회원 이메일
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private final String mbEmail;

    /**
     *TODO#3-2 회원 이름 검증
     *  - @NotBlank,  message = 이름은 필수 입력 항목입니다.
     *  - @Size(min,max), message = 이름은 2자 이상 20자 이하로 입력해주세요.
     */
    // 회원 이름
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요.")
    private final String mbName;

    /**
     * TODO#3-3 비밀번호 검증
     *  - @NotBlank, message = 비밀번호는 필수 입력 항목입니다.
     *  - @Size(min,max), 비밀번호는 8자 이상 20자 이하로 입력해주세요.
     *  - @Pattern(regexp, message), 비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.
     *      - regexp , 위 조건을 만족하는 정규표현식 작성
     *      - 참고 : https://ko.wikipedia.org/wiki/%EC%A0%95%EA%B7%9C_%ED%91%9C%ED%98%84%EC%8B%9D
     */
    // 회원 비밀번호
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private final String mbPassword;

    /**
     * TODO#3-4 비밀번호 확인 검증
     * - 검증은 mbPassword와 동일 합니다.
     */
    // 회원 비밀번호 확인
    @NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호 확인은 8자 이상 20자 이하로 입력해주세요.")
    private final String mbPasswordConfirm;

    /**
     * TODO#3-5 모바일 연락처 검증
     * - @NotBlank ,  message = 모바일 연락처는 필수 입력 항목입니다.
     * - @Pattern(regexp, message)
     *      *      - regexp = 010-1234-5678 형태로 입력받을 수 있도록 정규 표현식을 작성하세요.
     *      *      - 참고 : https://ko.wikipedia.org/wiki/%EC%A0%95%EA%B7%9C_%ED%91%9C%ED%98%84%EC%8B%9D
     *
     */
    // 모바일 연락처
    @NotBlank(message = "모바일 연락처는 필수 입력 항목입니다.")
    @Pattern(
            regexp = "^01[0-9]-\\d{3,4}-\\d{4}$",
            message = "모바일 연락처는 01X-XXXX-XXXX 형식으로 입력해주세요."
    )
    private final String mbMobile;


    /**
     * TODO#3-6
     * @AssertTrue , message = '비밀번호와 비밀번호 확인이 일치하지 않습니다.'
     *  - mbPassword != null 이면 false를 반환 합니다.
     *  -  mbPassword == mbPasswordConfirm 조건이면 true를 응답 합니다.
     *  - true가 반환되면  검증이 통과 했다는 의미 입니다.
     *  - true가 반환되지 않다면 비밀번호 != 비밀번호 확인 이라는 의미이며 검증을 통과하지 못합니다.
     */
    // 비밀번호와 비밀번호 확인이 동일한지 검증
    @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
    public boolean isPasswordMatch() {
        return mbPassword != null && mbPassword.equals(mbPasswordConfirm);
    }
}
