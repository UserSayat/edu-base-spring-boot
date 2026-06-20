package com.example.edu_base.service.auth;

import com.example.edu_base.dto.auth.LoginRequest;
import com.example.edu_base.dto.auth.LoginResponse;
import com.example.edu_base.dto.auth.RegisterRequest;
import com.example.edu_base.dto.auth.RegisterResponse;
import com.example.edu_base.entity.User;
import com.example.edu_base.exception.UnauthenticatedException;
import com.example.edu_base.exception.UnauthorizedException;
import com.example.edu_base.repository.user.IUserRepository;
import com.example.edu_base.security.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
@Slf4j
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(IUserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        log.info("register user: {}", request.getUsername());
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("user: {}, already exists", request.getUsername());
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User(null,
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                true,
                request.getRole(),
                ZonedDateTime.now(ZoneOffset.UTC),
                ZonedDateTime.now(ZoneOffset.UTC)
                );

        return toRegisterResponse(userRepository.save(user));
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("login user: {}", request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> {
            log.warn("user: {}, not found", request.getUsername());
            return new UnauthenticatedException("Wrong login or password");
        });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Wrong password for user: {}", user.getUsername());
            throw new UnauthorizedException("Wrong login or password");
        }

        String token = jwtTokenService.generateToken(user.getUsername());

        return new LoginResponse(user.getUsername(), user.getRole(), token);
    }

    private RegisterResponse toRegisterResponse(User user) {
        return new RegisterResponse(user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
