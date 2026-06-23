package com.hr24.employee.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.dto.hr.EmployeeSensitiveInfoResponseDto;
import com.hr24.employee.dto.hr.EmployeeSensitiveInfoUpdateRequestDto;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.global.crypto.CryptoService;
import com.hr24.global.crypto.MaskingUtil;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeSensitiveInfoService {

	private final UserRepository userRepository;
	private final CryptoService cryptoService;

	// 민감정보 원본 조회 - 눈 버튼 클릭 시 사용
	public EmployeeSensitiveInfoResponseDto findSensitiveInfo(Long employeeId) {
		User user = findUser(employeeId);

		return EmployeeSensitiveInfoResponseDto.of(
				user.getBankName(),
				decrypt(user.getAccountNumber()),
				user.getAccountHolder(),
				decrypt(user.getRrn())
		);
	}

	// 민감정보 수정 - 계좌번호, 주민등록번호는 암호화해서 저장
	@Transactional
	public EmployeeSensitiveInfoResponseDto updateSensitiveInfo(
			Long employeeId,
			EmployeeSensitiveInfoUpdateRequestDto request
	) {
		User user = findUser(employeeId);

		String bankName = normalize(request.getBankName());
		String accountNumber = normalize(request.getAccountNumber());
		String accountHolder = normalize(request.getAccountHolder());
		String rrn = normalize(request.getRrn());

		user.updateSensitiveInfo(
				bankName,
				cryptoService.encrypt(accountNumber),
				accountHolder,
				cryptoService.encrypt(rrn)
		);

		return EmployeeSensitiveInfoResponseDto.of(
				bankName,
				accountNumber,
				accountHolder,
				rrn
		);
	}

	// 직원 상세 조회 기본 표시용 - 계좌번호 마스킹
	public String getMaskedAccountNumber(User user) {
		if (user == null) {
			return null;
		}

		String accountNumber = decrypt(user.getAccountNumber());

		return MaskingUtil.maskAccountNumber(accountNumber);
	}

	// 직원 상세 조회 기본 표시용 - 주민등록번호 마스킹
	public String getMaskedRrn(User user) {
		if (user == null) {
			return null;
		}

		String rrn = decrypt(user.getRrn());

		return MaskingUtil.maskRrn(rrn);
	}

	private User findUser(Long employeeId) {
		return userRepository.findById(employeeId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	}

	private String decrypt(String encryptedValue) {
		if (encryptedValue == null || encryptedValue.isBlank()) {
			return null;
		}

		return cryptoService.decrypt(encryptedValue);
	}

	private String normalize(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}
}