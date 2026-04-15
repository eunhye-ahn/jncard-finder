package com.esstudy.jncardsearch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String homeAddress;
}
