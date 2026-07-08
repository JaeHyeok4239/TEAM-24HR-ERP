package com.hr24.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hr24.auth.dto.LoginRequestDto;
import com.hr24.auth.dto.LoginTokenDto;
import com.hr24.auth.dto.RefreshTokenResponseDto;
import com.hr24.auth.jwt.JwtProvider;
import com.hr24.auth.util.PasswordResetTokenGenerator;
import com.hr24.auth.util.VerificationCodeGenerator;
import com.hr24.employee.entity.Role;
import com.hr24.employee.entity.User;
import com.hr24.employee.entity.UserRole;
import com.hr24.employee.repository.UserRepository;
import com.hr24.employee.repository.UserRoleRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;
import com.hr24.global.mail.AccountMailService;
import com.hr24.global.redis.RedisService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RedisService redisService;

    @Mock
    private VerificationCodeGenerator verificationCodeGenerator;

    @Mock
    private PasswordResetTokenGenerator passwordResetTokenGenerator;

    @Mock
    private AccountMailService accountMailService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("로그인 성공 시 Access Token과 Refresh Token을 발급하고 Refresh Token을 세션 단위 Redis Key로 저장한다")
    void login_success() {
        // given
        LoginRequestDto request = new LoginRequestDto();
        request.setLoginId("hong");
        request.setPassword("raw-password");

        User user = createUser(1L, "hong", "encoded-password");

        when(userRepository.findByLoginId("hong"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("raw-password", "encoded-password"))
                .thenReturn(true);

        when(userRoleRepository.findAllWithRoleByEmployeeId(1L))
                .thenReturn(List.of(
                        createUserRole("USER"),
                        createUserRole("HR")
                ));

        when(jwtProvider.createAccessToken(
                1L,
                "hong",
                List.of("USER", "HR")
        )).thenReturn("access-token");

        when(jwtProvider.createRefreshToken(eq(1L), anyString()))
                .thenReturn("refresh-token");

        // when
        LoginTokenDto result = authService.login(request);

        // then
        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");

        verify(redisService).save(
                argThat(key -> key.startsWith("RT:1:")),
                eq("refresh-token"),
                eq(7L),
                eq(TimeUnit.DAYS)
        );
    }

    @Test
    @DisplayName("로그인 시 비밀번호가 일치하지 않으면 예외가 발생한다")
    void login_fail_invalidPassword() {
        // given
        LoginRequestDto request = new LoginRequestDto();
        request.setLoginId("hong");
        request.setPassword("wrong-password");

        User user = createUser(1L, "hong", "encoded-password");

        when(userRepository.findByLoginId("hong"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong-password", "encoded-password"))
                .thenReturn(false);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.login(request)
        );

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.INVALID_PASSWORD);

        verify(jwtProvider, never()).createAccessToken(
                eq(1L),
                eq("hong"),
                anyList()
        );
    }

    @Test
    @DisplayName("Refresh Token이 유효하고 세션 단위 Redis 저장값과 일치하면 Access Token을 재발급한다")
    void refresh_success() {
        // given
        String refreshToken = "refresh-token";

        User user = createUser(1L, "hong", "encoded-password");

        when(jwtProvider.validateToken(refreshToken))
                .thenReturn(true);

        when(jwtProvider.getTokenType(refreshToken))
                .thenReturn("REFRESH");

        when(jwtProvider.getEmployeeId(refreshToken))
                .thenReturn(1L);

        when(jwtProvider.getSessionId(refreshToken))
                .thenReturn("session-1");

        when(redisService.get("RT:1:session-1"))
                .thenReturn(refreshToken);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRoleRepository.findAllWithRoleByEmployeeId(1L))
                .thenReturn(List.of(
                        createUserRole("USER"),
                        createUserRole("HR")
                ));

        when(jwtProvider.createAccessToken(
                1L,
                "hong",
                List.of("USER", "HR")
        )).thenReturn("new-access-token");

        // when
        RefreshTokenResponseDto result = authService.refresh(refreshToken);

        // then
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
    }

    @Test
    @DisplayName("Refresh Token이 세션 단위 Redis 저장값과 다르면 Access Token 재발급에 실패한다")
    void refresh_fail_mismatchRedisToken() {
        // given
        String refreshToken = "refresh-token";

        when(jwtProvider.validateToken(refreshToken))
                .thenReturn(true);

        when(jwtProvider.getTokenType(refreshToken))
                .thenReturn("REFRESH");

        when(jwtProvider.getEmployeeId(refreshToken))
                .thenReturn(1L);

        when(jwtProvider.getSessionId(refreshToken))
                .thenReturn("session-1");

        when(redisService.get("RT:1:session-1"))
                .thenReturn("other-refresh-token");

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.refresh(refreshToken)
        );

        // then
        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.INVALID_TOKEN);

        verify(jwtProvider, never()).createAccessToken(
                eq(1L),
                eq("hong"),
                anyList()
        );
    }

    @Test
    @DisplayName("로그아웃 시 현재 세션의 Refresh Token만 Redis에서 삭제한다")
    void logout_success_deleteRefreshToken() {
        // given
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(jwtProvider.validateToken(refreshToken))
                .thenReturn(true);

        when(jwtProvider.getTokenType(refreshToken))
                .thenReturn("REFRESH");

        when(jwtProvider.getEmployeeId(refreshToken))
                .thenReturn(1L);

        when(jwtProvider.getSessionId(refreshToken))
                .thenReturn("session-1");

        // when
        authService.logout(accessToken, refreshToken);

        // then
        verify(redisService).delete("RT:1:session-1");
    }

    private User createUser(
            Long employeeId,
            String loginId,
            String password
    ) {
        User user = new User();
        user.setEmployeeId(employeeId);
        user.setLoginId(loginId);
        user.setPassword(password);
        user.setName("홍길동");
        user.setEmail("hong@example.com");

        return user;
    }

    private UserRole createUserRole(String roleCode) {
        Role role = new Role();
        role.setRoleCode(roleCode);
        role.setRoleName(roleCode);

        UserRole userRole = new UserRole();
        userRole.setRole(role);

        return userRole;
    }
}