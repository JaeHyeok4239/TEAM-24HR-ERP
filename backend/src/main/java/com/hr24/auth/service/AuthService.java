package com.hr24.auth.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hr24.auth.dto.LoginRequestDto;
import com.hr24.auth.dto.LoginTokenDto;
import com.hr24.auth.dto.RefreshTokenResponseDto;
import com.hr24.auth.jwt.JwtProvider;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.employee.repository.UserRoleRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;
import com.hr24.global.redis.RedisService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserRoleRepository userRoleRepository;
	private final JwtProvider jwtProvider;
	private final RedisService redisService;
	
	public LoginTokenDto login(LoginRequestDto requestDto) {
		
		User user = userRepository.findByLoginId(requestDto.getLoginId())
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		
		boolean passwordMatched = passwordEncoder.matches(
				requestDto.getPassword(),
				user.getPassword()
		);
		
		if (!passwordMatched) {
			throw new BusinessException(ErrorCode.INVALID_PASSWORD);
		}
		
		List<String> roles = getRoleCodes(user.getEmployeeId());
		
		String accessToken = jwtProvider.createAccessToken(
		        user.getEmployeeId(),
		        user.getLoginId(),
		        roles
		);
		
		String refreshToken = 
				jwtProvider.createRefreshToken(
						user.getEmployeeId()
				);
		
		redisService.save(
				getRefreshTokenKey(user.getEmployeeId()),
		        refreshToken,
		        7,
		        TimeUnit.DAYS
		);
		
		return new LoginTokenDto(
				accessToken,
				refreshToken);
	}
	
	public RefreshTokenResponseDto refresh(String refreshToken) {
		
		if (refreshToken == null || refreshToken.isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
		
		if (!jwtProvider.validateToken(refreshToken)) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
		
		String tokenType = jwtProvider.getTokenType(refreshToken);
		
		if (!"REFRESH".equals(tokenType)) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
		
		Long employeeId = jwtProvider.getEmployeeId(refreshToken);
		
		String redisKey = getRefreshTokenKey(employeeId);
		
		String savedRefreshToken = redisService.get(redisKey);
		
		if (savedRefreshToken == null) {
		    throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}

		if (!savedRefreshToken.equals(refreshToken)) {
		    throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
		
		User user = userRepository.findById(employeeId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		
		List<String> roles = getRoleCodes(user.getEmployeeId());
		
		String newAccessToken = jwtProvider.createAccessToken(
		        user.getEmployeeId(),
		        user.getLoginId(),
		        roles
		);
		
		return new RefreshTokenResponseDto(newAccessToken);
	}
	
	public void logout(String accessToken, String refreshToken) {

        Long employeeId = resolveEmployeeIdFromRefreshToken(refreshToken);

        if (employeeId == null) {
            employeeId = resolveEmployeeIdFromAccessToken(accessToken);
        }

        if (employeeId == null) {
            return;
        }

        redisService.delete(
                getRefreshTokenKey(employeeId)
        );
    }

    private Long resolveEmployeeIdFromRefreshToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return null;
        }

        if (!jwtProvider.validateToken(refreshToken)) {
            return null;
        }

        if (!"REFRESH".equals(jwtProvider.getTokenType(refreshToken))) {
            return null;
        }

        return jwtProvider.getEmployeeId(refreshToken);
    }

    private Long resolveEmployeeIdFromAccessToken(String accessToken) {

        if (accessToken == null || accessToken.isBlank()) {
            return null;
        }

        if (!jwtProvider.validateToken(accessToken)) {
            return null;
        }

        if (!"ACCESS".equals(jwtProvider.getTokenType(accessToken))) {
            return null;
        }

        return jwtProvider.getEmployeeId(accessToken);
    }

    private List<String> getRoleCodes(Long employeeId) {

        return userRoleRepository
                .findAllWithRoleByEmployeeId(employeeId)
                .stream()
                .map(userRole ->
                        userRole.getRole().getRoleCode()
                )
                .toList();
    }

    private String getRefreshTokenKey(Long employeeId) {
        return "RT:" + employeeId;
    }
}