package com.nhnacademy.blog.member.dto;

import jakarta.validation.constraints.*;
import lombok.ToString;
import lombok.Value;

@Value
@ToString
public class MemberRegisterRequest {

    // 회원 이메일
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private final String mbEmail;

    // 회원 이름
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요.")
    private final String mbName;

    // 회원 비밀번호
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private final String mbPassword;

    // 회원 비밀번호 확인
    @NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호 확인은 8자 이상 20자 이하로 입력해주세요.")
    private final String mbPasswordConfirm;

    // 모바일 연락처
    @NotBlank(message = "모바일 연락처는 필수 입력 항목입니다.")
    @Pattern(
            regexp = "^01[0-9]-\\d{3,4}-\\d{4}$",
            message = "모바일 연락처는 01X-XXXX-XXXX 형식으로 입력해주세요."
    )
    private final String mbMobile;

    // 비밀번호와 비밀번호 확인이 동일한지 검증
    @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
    public boolean isPasswordMatch() {
        return mbPassword != null && mbPassword.equals(mbPasswordConfirm);
    }

}
