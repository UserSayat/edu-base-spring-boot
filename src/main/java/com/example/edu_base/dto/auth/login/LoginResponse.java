package com.example.edu_base.dto.auth.login;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String username;
    private Set<String> roles;
    private String accessToken;
    private String refreshToken;
}
