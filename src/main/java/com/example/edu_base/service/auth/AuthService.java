package com.example.edu_base.service.auth;

import com.example.edu_base.common.Role;
import com.example.edu_base.dto.auth.login.LoginRequest;
import com.example.edu_base.dto.auth.login.LoginResponse;
import com.example.edu_base.dto.auth.login.RefreshTokenRequest;
import com.example.edu_base.dto.auth.register.RegisterRequest;
import com.example.edu_base.dto.auth.register.RegisterResponse;
import com.example.edu_base.entity.User;
import com.example.edu_base.exception.UnauthenticatedException;
import com.example.edu_base.exception.UnauthorizedException;
import com.example.edu_base.repository.user.IUserRepository;
import com.example.edu_base.security.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthService(IUserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
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
                Set.of(Role.valueOf(request.getRole())),
                ZonedDateTime.now(ZoneOffset.UTC),
                ZonedDateTime.now(ZoneOffset.UTC)
                );

        return toRegisterResponse(userRepository.save(user));
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("login user: {}", request.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();

//            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//                log.warn("Wrong password for user: {}", user.getUsername());
//                throw new UnauthorizedException("Wrong login or password");
//            } Теперь это делает authenticationManager.authenticate(...)

            if (!user.isActive()) {
                log.warn("User account is disabled: {}", user.getUsername());
                throw new UnauthenticatedException("User account is disabled");
            }

            String accessToken = jwtTokenService.generateAccessToken(user);
            String refreshToken = jwtTokenService.generateRefreshToken(user);

            return new LoginResponse(user.getUsername(),
                    user.getRoles().stream()
                            .map(Role::name)
                            .collect(Collectors.toSet()),
                    accessToken,
                    refreshToken);
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            log.warn("Invalid credentials for user: {}", request.getUsername());
            throw new UnauthenticatedException("Wrong login or password");

        }catch (Exception e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.warn("User: {}, not found", request.getUsername(), e);
            throw new UnauthenticatedException(message, e, 20001, null);
        }
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String username = jwtTokenService.extractUsername(request.getRefreshToken());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User: {}, doesn't exist", username);
                    return new UnauthenticatedException("User not found");
                });

        if (!jwtTokenService.isRefreshTokenValid(request.getRefreshToken(), user)) {
            log.warn("Invalid token: {}, user: {}", request.getRefreshToken(), user);
            throw new UnauthorizedException("Invalid refresh token");
        }

        String newAccessToken = jwtTokenService.generateAccessToken(user);
        String newRefreshToken = jwtTokenService.generateRefreshToken(user);

        return new LoginResponse(user.getUsername(),
                user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toSet()),
                newAccessToken,
                newRefreshToken);
    }

    private RegisterResponse toRegisterResponse(User user) {
        return new RegisterResponse(user.getId(),
                user.getUsername(),
                user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toSet()),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
