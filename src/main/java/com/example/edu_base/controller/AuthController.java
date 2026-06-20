package com.example.edu_base.controller;

import com.example.edu_base.common.CommonResponse;
import com.example.edu_base.dto.auth.LoginRequest;
import com.example.edu_base.dto.auth.LoginResponse;
import com.example.edu_base.dto.auth.RegisterRequest;
import com.example.edu_base.dto.auth.RegisterResponse;
import com.example.edu_base.service.auth.IAuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("request to register from user: {}", request.getUsername());
        RegisterResponse response = authService.register(request);
        log.info("user id:{}, username:{}, registered successfully", response.getId(), response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse<>(response));
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("request to login from user: {}", request.getUsername());
        LoginResponse response = authService.login(request);
        log.info("{}'s password passed successfully", response.getUsername());
        return ResponseEntity.ok(new CommonResponse<>(response));
    }
}
