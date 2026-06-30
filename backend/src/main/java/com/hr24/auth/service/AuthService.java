package com.hr24.auth.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.auth.dto.LoginRequestDto;
import com.hr24.auth.dto.LoginTokenDto;
import com.hr24.auth.dto.PasswordResetCodeSendRequestDto;
import com.hr24.auth.dto.PasswordResetCodeVerifyRequestDto;
import com.hr24.auth.dto.PasswordResetCodeVerifyResponseDto;
import com.hr24.auth.dto.PasswordResetRequestDto;
import com.hr24.auth.dto.RefreshTokenResponseDto;
import com.hr24.auth.jwt.JwtProvider;
import com.hr24.auth.util.PasswordResetTokenGenerator;
import com.hr24.auth.util.VerificationCodeGenerator;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.employee.repository.UserRoleRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;
import com.hr24.global.mail.AccountMailService;
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
	private final VerificationCodeGenerator verificationCodeGenerator;
	private final PasswordResetTokenGenerator passwordResetTokenGenerator;
	private final AccountMailService accountMailService;
	
	public LoginTokenDto login(LoginRequestDto requestDto) {
		
		User user = userRepository.findByLoginId(requestDto.getLoginId())
				.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PASSWORD));
		
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
	
	public void sendPasswordResetCode(PasswordResetCodeSendRequestDto requestDto) {

	    String loginId = requestDto.getLoginId().trim();
	    String email = requestDto.getEmail().trim();

	    User user = userRepository.findByLoginIdIgnoreCaseAndEmailIgnoreCase(loginId, email)
	            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PASSWORD_RESET_INFO));

	    String code = verificationCodeGenerator.generate();

	    redisService.save(
	            getPasswordResetCodeKey(user.getEmployeeId()),
	            code,
	            5,
	            TimeUnit.MINUTES
	    );

	    accountMailService.sendPasswordResetCodeMail(
	            user.getEmail(),
	            user.getName(),
	            code
	    );
	}
	
	public PasswordResetCodeVerifyResponseDto verifyPasswordResetCode(
	        PasswordResetCodeVerifyRequestDto requestDto
	) {
	    String loginId = requestDto.getLoginId().trim();
	    String email = requestDto.getEmail().trim();
	    String inputCode = requestDto.getCode().trim();

	    User user = userRepository.findByLoginIdIgnoreCaseAndEmailIgnoreCase(loginId, email)
	            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PASSWORD_RESET_INFO));

	    String codeKey = getPasswordResetCodeKey(user.getEmployeeId());
	    String savedCode = redisService.get(codeKey);

	    if (savedCode == null || !savedCode.equals(inputCode)) {
	        throw new BusinessException(ErrorCode.INVALID_PASSWORD_RESET_CODE);
	    }

	    String resetToken = passwordResetTokenGenerator.generate();

	    redisService.save(
	            getPasswordResetTokenKey(resetToken),
	            String.valueOf(user.getEmployeeId()),
	            10,
	            TimeUnit.MINUTES
	    );

	    redisService.delete(codeKey);

	    return new PasswordResetCodeVerifyResponseDto(resetToken);
	}
	
	@Transactional
	public void resetPassword(PasswordResetRequestDto requestDto) {

	    if (!requestDto.getNewPassword().equals(requestDto.getNewPasswordConfirm())) {
	        throw new BusinessException(ErrorCode.INVALID_PASSWORD);
	    }

	    String resetToken = requestDto.getResetToken().trim();

	    String employeeIdValue = redisService.get(
	            getPasswordResetTokenKey(resetToken)
	    );

	    if (employeeIdValue == null) {
	        throw new BusinessException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN);
	    }

	    Long employeeId = Long.valueOf(employeeIdValue);

	    User user = userRepository.findById(employeeId)
	            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

	    if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPassword())) {
	        throw new BusinessException(ErrorCode.SAME_AS_OLD_PASSWORD);
	    }
	    
	    String encodedPassword = passwordEncoder.encode(
	            requestDto.getNewPassword()
	    );

	    user.changePassword(encodedPassword);

	    redisService.delete(
	            getPasswordResetTokenKey(resetToken)
	    );

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
    
    private String getPasswordResetCodeKey(Long employeeId) {
        return "PWD_RESET_CODE:" + employeeId;
    }

    private String getPasswordResetTokenKey(String resetToken) {
        return "PWD_RESET_TOKEN:" + resetToken;
    }
}