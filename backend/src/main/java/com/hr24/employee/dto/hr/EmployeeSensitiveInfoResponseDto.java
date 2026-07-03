package com.hr24.employee.dto.hr;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmployeeSensitiveInfoResponseDto {

	private String bankName;
	private String accountNumber;
	private String accountHolder;
	private String rrn;

	public static EmployeeSensitiveInfoResponseDto of(
			String bankName,
			String accountNumber,
			String accountHolder,
			String rrn
	) {
		EmployeeSensitiveInfoResponseDto response = new EmployeeSensitiveInfoResponseDto();
		response.bankName = bankName;
		response.accountNumber = accountNumber;
		response.accountHolder = accountHolder;
		response.rrn = rrn;
		return response;
	}
}