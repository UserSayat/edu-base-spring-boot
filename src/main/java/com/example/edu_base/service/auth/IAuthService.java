package com.example.edu_base.service.auth;

import com.example.edu_base.dto.auth.LoginRequest;
import com.example.edu_base.dto.auth.LoginResponse;
import com.example.edu_base.dto.auth.RegisterRequest;
import com.example.edu_base.dto.auth.RegisterResponse;

public interface IAuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
