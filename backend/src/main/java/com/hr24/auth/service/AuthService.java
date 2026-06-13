package com.hr24.auth.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hr24.auth.dto.LoginRequestDto;
import com.hr24.auth.dto.LoginResponseDto;
import com.hr24.auth.dto.RefreshTokenRequestDto;
import com.hr24.auth.dto.RefreshTokenResponseDto;
import com.hr24.auth.jwt.JwtProvider;
import com.hr24.employee.entity.Role;
import com.hr24.employee.entity.User;
import com.hr24.employee.entity.UserRole;
import com.hr24.employee.repository.RoleRepository;
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
	private final RoleRepository roleRepository;
	private final JwtProvider jwtProvider;
	private final RedisService redisService;
	
	public LoginResponseDto login(LoginRequestDto requestDto) {
		
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
		
		return new LoginResponseDto(
				accessToken,
				refreshToken);
	}
	
	public RefreshTokenResponseDto refresh(RefreshTokenRequestDto requestDto) {
		
		String refreshToken = requestDto.getRefreshToken();
		
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
	
	public void logout(String accessToken) {
		
		Long employeeId =
				jwtProvider.getEmployeeId(accessToken);
		
		redisService.delete(
		        getRefreshTokenKey(employeeId)
		);
	}
	
	private List<String> getRoleCodes(Long employeeId) {

	    List<UserRole> userRoles =
	            userRoleRepository.findByEmployeeId(employeeId);

	    List<Long> roleIds = userRoles.stream()
	            .map(UserRole::getRoleId)
	            .toList();

	    return roleRepository.findByRoleIdIn(roleIds)
	            .stream()
	            .map(Role::getRoleCode)
	            .toList();
	}

	private String getRefreshTokenKey(Long employeeId) {
	    return "RT:" + employeeId;
	}
	
}
