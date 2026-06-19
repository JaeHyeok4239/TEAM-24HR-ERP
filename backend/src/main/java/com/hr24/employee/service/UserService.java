package com.hr24.employee.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.dto.user.MyInfoResponseDto;
import com.hr24.employee.dto.user.MyInfoUpdateRequestDto;
import com.hr24.employee.dto.user.PasswordChangeRequestDto;
import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.Position;
import com.hr24.employee.entity.User;
import com.hr24.employee.entity.UserRole;
import com.hr24.employee.repository.UserRepository;
import com.hr24.employee.repository.UserRoleRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;
import com.hr24.global.redis.RedisService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    @Transactional(readOnly = true)
    public MyInfoResponseDto getMyInfo() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String loginId = authentication.getName();

        User user = userRepository
                .findByLoginIdWithDepartmentAndPosition(loginId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND)
                );

        return createMyInfoResponse(user);
    }
    
    @Transactional
    public MyInfoResponseDto updateMyInfo(
    		MyInfoUpdateRequestDto requestDto
    ) {
    	Authentication authentication =
    			SecurityContextHolder.getContext().getAuthentication();
    	
    	String loginId = authentication.getName();
    	
    	User user = userRepository
    			.findByLoginIdWithDepartmentAndPosition(loginId)
    			.orElseThrow(() ->
    					new BusinessException(ErrorCode.USER_NOT_FOUND)
    			);
    	
    	user.updateMyInfo(
    			requestDto.getEmail(),
    			requestDto.getPhone(),
    			requestDto.getZipcode(),
    			requestDto.getAddress(),
    			requestDto.getAddressDetail()
    	);
    	
    	return createMyInfoResponse(user);
    }
    
    @Transactional
    public void changePassword(
    		PasswordChangeRequestDto requestDto
    ) {
    	Authentication authentication =
    			SecurityContextHolder.getContext().getAuthentication();
    	
    	String loginId = authentication.getName();
    	
    	User user = userRepository.findByLoginId(loginId)
    			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)
    			);
    	
    	boolean currentPasswordMatched = passwordEncoder.matches(
    			requestDto.getCurrentPassword(),
    			user.getPassword()
    	);
    	
    	if (!currentPasswordMatched) {
    		throw new BusinessException(ErrorCode.CURRENT_PASSWORD_MISMATCH);
    	}
    	
    	boolean sameAsCurrentPassword = passwordEncoder.matches(
    			requestDto.getNewPassword(),
    			user.getPassword()
    	);
    	
    	if (sameAsCurrentPassword) {
    		throw new BusinessException(ErrorCode.SAME_AS_CURRENT_PASSWORD);
    	}
    	
    	String encodedPasswod = passwordEncoder.encode(requestDto.getNewPassword());
    	
    	user.changePassword(encodedPasswod);
    	
    	redisService.delete("RT:" + user.getEmployeeId());
    }

    private MyInfoResponseDto createMyInfoResponse(User user) {

        Department department = user.getDepartment();
        Position position = user.getPosition();

        List<UserRole> userRoles =
                userRoleRepository.findAllWithRoleByEmployeeId(
                        user.getEmployeeId()
                );

        List<String> roles = userRoles.stream()
                .map(userRole ->
                        userRole.getRole().getRoleCode()
                )
                .toList();

        return new MyInfoResponseDto(
                user.getEmployeeNo(),
                user.getLoginId(),
                user.getName(),

                department != null
                        ? department.getDepartmentName()
                        : null,

                position != null
                        ? position.getPositionName()
                        : null,

                user.getHireDate() != null
                        ? user.getHireDate().toLocalDate()
                        : null,

                user.getEmail(),
                user.getPhone(),
                user.getZipcode(),
                user.getAddress(),
                user.getAddressDetail(),

                roles
        );
    } 
}