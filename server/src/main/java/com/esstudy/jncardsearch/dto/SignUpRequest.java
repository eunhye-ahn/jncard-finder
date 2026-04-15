package com.esstudy.jncardsearch.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "이름을 입력하세요")
    private String name;

    @NotBlank(message = "이메일을 입력하세요")
    @Email(message = "이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요")
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
    message="비밀번호 8자 이상, 영문+숫자 조합이어야 합니다")
    private String password;

    private String homeAddress;
}