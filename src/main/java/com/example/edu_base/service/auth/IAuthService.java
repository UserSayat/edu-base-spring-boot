package com.example.edu_base.service.auth;

import com.example.edu_base.dto.auth.login.LoginRequest;
import com.example.edu_base.dto.auth.login.LoginResponse;
import com.example.edu_base.dto.auth.login.RefreshTokenRequest;
import com.example.edu_base.dto.auth.register.RegisterRequest;
import com.example.edu_base.dto.auth.register.RegisterResponse;

public interface IAuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    LoginResponse refreshToken(RefreshTokenRequest request);
}
