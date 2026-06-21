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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Unit Tests")
class AuthServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private User user;
    private final String USERNAME = "testuser";
    private final String PASSWORD = "password123";
    private final String ENCODED_PASSWORD = "encoded_password";
    private final String ACCESS_TOKEN = "access_token_123";
    private final String REFRESH_TOKEN = "refresh_token_456";
    private final Long USER_ID = 1L;
    private final Set<Role> ROLES = Set.of(Role.ROLE_STUDENT);

    @BeforeEach
    void setUp() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        registerRequest = new RegisterRequest();

        registerRequest.setUsername(USERNAME);
        registerRequest.setPassword(PASSWORD);

        loginRequest = new LoginRequest();

        loginRequest.setUsername(USERNAME);
        loginRequest.setPassword(PASSWORD);

        refreshTokenRequest = new RefreshTokenRequest();

        refreshTokenRequest.setRefreshToken(REFRESH_TOKEN);

        user = new User(
                USER_ID,
                USERNAME,
                ENCODED_PASSWORD,
                true,
                ROLES,
                now,
                now
        );
    }


    //register

    @Test
    @DisplayName("Should register user successfully when username is unique")
    void register_Success() {
        // Given
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        RegisterResponse response = authService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals(USER_ID, response.getId());
        assertEquals(USERNAME, response.getUsername());
        assertEquals(Set.of("ROLE_STUDENT"), response.getRole());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(passwordEncoder, times(1)).encode(PASSWORD);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when username already exists")
    void register_UsernameExists_ThrowsIllegalArgumentException() {
        // Given
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("User already exists", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should set default role STUDENT when registering")
    void register_SetsDefaultRole() {
        // Given
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        authService.register(registerRequest);

        // Then
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getRoles().contains(Role.ROLE_STUDENT) &&
                        savedUser.getRoles().size() == 1
        ));
    }

    @Test
    @DisplayName("Should set user as active when registering")
    void register_SetsUserActive() {
        // Given
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        authService.register(registerRequest);

        // Then
        verify(userRepository).save(argThat(savedUser ->
                savedUser.isActive() == true
        ));
    }

    @Test
    @DisplayName("Should set correct timestamps when registering")
    void register_SetsCorrectTimestamps() {
        // Given
        ZonedDateTime beforeTest = ZonedDateTime.now(ZoneOffset.UTC);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        RegisterResponse response = authService.register(registerRequest);

        // Then
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertEquals(ZoneOffset.UTC, response.getCreatedAt().getOffset());
        assertEquals(ZoneOffset.UTC, response.getUpdatedAt().getOffset());
        assertTrue(response.getCreatedAt().isAfter(beforeTest.minusSeconds(1)));
    }

    @Test
    @DisplayName("Should encode password before saving")
    void register_EncodesPassword() {
        // Given
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        authService.register(registerRequest);

        // Then
        verify(passwordEncoder, times(1)).encode(PASSWORD);
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getPassword().equals(ENCODED_PASSWORD)
        ));
    }


    //login

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void login_Success() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtTokenService.generateAccessToken(user)).thenReturn(ACCESS_TOKEN);
        when(jwtTokenService.generateRefreshToken(user)).thenReturn(REFRESH_TOKEN);

        // When
        LoginResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(USERNAME, response.getUsername());
        assertEquals(Set.of("ROLE_STUDENT"), response.getRoles());
        assertEquals(ACCESS_TOKEN, response.getAccessToken());
        assertEquals(REFRESH_TOKEN, response.getRefreshToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenService, times(1)).generateAccessToken(user);
        verify(jwtTokenService, times(1)).generateRefreshToken(user);
    }

    @Test
    @DisplayName("Should throw UnauthenticatedException when credentials are invalid")
    void login_InvalidCredentials_ThrowsUnauthenticatedException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        UnauthenticatedException exception = assertThrows(UnauthenticatedException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Wrong login or password", exception.getMessage());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenService, never()).generateAccessToken(any(User.class));
        verify(jwtTokenService, never()).generateRefreshToken(any(User.class));
    }

    @Test
    @DisplayName("Should throw UnauthenticatedException when user not found")
    void login_UserNotFound_ThrowsUnauthenticatedException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // When & Then
        UnauthenticatedException exception = assertThrows(UnauthenticatedException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Wrong login or password", exception.getMessage());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenService, never()).generateAccessToken(any(User.class));
        verify(jwtTokenService, never()).generateRefreshToken(any(User.class));
    }

    @Test
    @DisplayName("Should throw UnauthenticatedException when user account is disabled")
    void login_UserDisabled_ThrowsUnauthenticatedException() {
        // Given
        User disabledUser = new User(
                USER_ID,
                USERNAME,
                ENCODED_PASSWORD,
                false, // Disabled
                ROLES,
                ZonedDateTime.now(ZoneOffset.UTC),
                ZonedDateTime.now(ZoneOffset.UTC)
        );

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(disabledUser);

        // When & Then
        UnauthenticatedException exception = assertThrows(UnauthenticatedException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("User account is disabled", exception.getMessage());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenService, never()).generateAccessToken(any(User.class));
        verify(jwtTokenService, never()).generateRefreshToken(any(User.class));
    }

    @Test
    @DisplayName("Should generate both access and refresh tokens on successful login")
    void login_GeneratesBothTokens() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtTokenService.generateAccessToken(user)).thenReturn(ACCESS_TOKEN);
        when(jwtTokenService.generateRefreshToken(user)).thenReturn(REFRESH_TOKEN);

        // When
        LoginResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertNotEquals(response.getAccessToken(), response.getRefreshToken());
    }

    @Test
    @DisplayName("Should handle generic exception during login")
    void login_GenericException_ThrowsUnauthenticatedException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        UnauthenticatedException exception = assertThrows(UnauthenticatedException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals(20001, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Unexpected error"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenService, never()).generateAccessToken(any(User.class));
        verify(jwtTokenService, never()).generateRefreshToken(any(User.class));
    }

    @Test
    @DisplayName("Should include roles in login response")
    void login_IncludesRoles() {
        // Given
        Set<Role> roles = Set.of(Role.ROLE_STUDENT, Role.ROLE_TEACHER);
        User userWithRoles = new User(
                USER_ID,
                USERNAME,
                ENCODED_PASSWORD,
                true,
                roles,
                ZonedDateTime.now(ZoneOffset.UTC),
                ZonedDateTime.now(ZoneOffset.UTC)
        );

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userWithRoles);
        when(jwtTokenService.generateAccessToken(userWithRoles)).thenReturn(ACCESS_TOKEN);
        when(jwtTokenService.generateRefreshToken(userWithRoles)).thenReturn(REFRESH_TOKEN);

        // When
        LoginResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getRoles().size());
        assertTrue(response.getRoles().contains("ROLE_STUDENT"));
        assertTrue(response.getRoles().contains("ROLE_TEACHER"));
    }


    //refreshToken

    @Test
    @DisplayName("Should refresh token successfully with valid refresh token")
    void refreshToken_Success() {
        // Given
        when(jwtTokenService.extractUsername(REFRESH_TOKEN)).thenReturn(USERNAME);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(jwtTokenService.isRefreshTokenValid(REFRESH_TOKEN, user)).thenReturn(true);
        when(jwtTokenService.generateAccessToken(user)).thenReturn("new_access_token");
        when(jwtTokenService.generateRefreshToken(user)).thenReturn("new_refresh_token");

        // When
        LoginResponse response = authService.refreshToken(refreshTokenRequest);

        // Then
        assertNotNull(response);
        assertEquals(USERNAME, response.getUsername());
        assertEquals(Set.of("ROLE_STUDENT"), response.getRoles());
        assertEquals("new_access_token", response.getAccessToken());
        assertEquals("new_refresh_token", response.getRefreshToken());

        verify(jwtTokenService, times(1)).extractUsername(REFRESH_TOKEN);
        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(jwtTokenService, times(1)).isRefreshTokenValid(REFRESH_TOKEN, user);
        verify(jwtTokenService, times(1)).generateAccessToken(user);
        verify(jwtTokenService, times(1)).generateRefreshToken(user);
    }

    @Test
    @DisplayName("Should throw UnauthenticatedException when user not found during refresh")
    void refreshToken_UserNotFound_ThrowsUnauthenticatedException() {
        // Given
        when(jwtTokenService.extractUsername(REFRESH_TOKEN)).thenReturn(USERNAME);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        // When & Then
        UnauthenticatedException exception = assertThrows(UnauthenticatedException.class, () -> {
            authService.refreshToken(refreshTokenRequest);
        });

        assertEquals("User not found", exception.getMessage());

        verify(jwtTokenService, times(1)).extractUsername(REFRESH_TOKEN);
        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(jwtTokenService, never()).isRefreshTokenValid(anyString(), any(User.class));
        verify(jwtTokenService, never()).generateAccessToken(any(User.class));
        verify(jwtTokenService, never()).generateRefreshToken(any(User.class));
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when refresh token is invalid")
    void refreshToken_InvalidToken_ThrowsUnauthorizedException() {
        // Given
        when(jwtTokenService.extractUsername(REFRESH_TOKEN)).thenReturn(USERNAME);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(jwtTokenService.isRefreshTokenValid(REFRESH_TOKEN, user)).thenReturn(false);

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authService.refreshToken(refreshTokenRequest);
        });

        assertEquals("Invalid refresh token", exception.getMessage());

        verify(jwtTokenService, times(1)).extractUsername(REFRESH_TOKEN);
        verify(userRepository, times(1)).findByUsername(USERNAME);
        verify(jwtTokenService, times(1)).isRefreshTokenValid(REFRESH_TOKEN, user);
        verify(jwtTokenService, never()).generateAccessToken(any(User.class));
        verify(jwtTokenService, never()).generateRefreshToken(any(User.class));
    }

    @Test
    @DisplayName("Should generate new tokens during refresh")
    void refreshToken_GeneratesNewTokens() {
        // Given
        String oldAccessToken = ACCESS_TOKEN;
        String oldRefreshToken = REFRESH_TOKEN;
        String newAccessToken = "new_access_token_123";
        String newRefreshToken = "new_refresh_token_456";

        when(jwtTokenService.extractUsername(oldRefreshToken)).thenReturn(USERNAME);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(jwtTokenService.isRefreshTokenValid(oldRefreshToken, user)).thenReturn(true);
        when(jwtTokenService.generateAccessToken(user)).thenReturn(newAccessToken);
        when(jwtTokenService.generateRefreshToken(user)).thenReturn(newRefreshToken);

        // When
        RefreshTokenRequest request = new RefreshTokenRequest();

        request.setRefreshToken(oldRefreshToken);

        LoginResponse response = authService.refreshToken(request);

        // Then
        assertEquals(newAccessToken, response.getAccessToken());
        assertEquals(newRefreshToken, response.getRefreshToken());
        assertNotEquals(oldAccessToken, response.getAccessToken());
        assertNotEquals(oldRefreshToken, response.getRefreshToken());
    }
}