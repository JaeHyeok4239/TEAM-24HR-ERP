package com.hr24.employee.dto.hr;

import java.time.LocalDateTime;
import java.util.List;

import com.hr24.employee.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeDetailResponseDto {

    private Long employeeId;
    private String employeeNo;
    private String loginId;
    private String name;
    private String phone;
    private String email;

    private String zipcode;
    private String address;
    private String addressDetail;

    private Long departmentId;
    private String departmentName;

    private Long positionId;
    private String positionName;

    private String employmentType;
    private String status;

    private LocalDateTime hireDate;
    private LocalDateTime resignationDate;

    private String rrn;
    private String bankName;
    private String accountNumber;
    private String accountHolder;

    private List<String> roles;

    public static EmployeeDetailResponseDto from(User user, List<String> roles) {
        return new EmployeeDetailResponseDto(
                user.getEmployeeId(),
                user.getEmployeeNo(),
                user.getLoginId(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),

                user.getZipcode(),
                user.getAddress(),
                user.getAddressDetail(),

                user.getDepartment() != null
                        ? user.getDepartment().getDepartmentId()
                        : null,
                user.getDepartment() != null
                        ? user.getDepartment().getDepartmentName()
                        : null,

                user.getPosition() != null
                        ? user.getPosition().getPositionId()
                        : null,
                user.getPosition() != null
                        ? user.getPosition().getPositionName()
                        : null,

                user.getEmploymentType() != null
                        ? user.getEmploymentType().name()
                        : null,
                user.getStatus() != null
                        ? user.getStatus().name()
                        : null,

                user.getHireDate(),
                user.getResignationDate(),

                user.getRrn(),
                user.getBankName(),
                user.getAccountNumber(),
                user.getAccountHolder(),

                roles
        );
    }
    
    public static EmployeeDetailResponseDto from(
    		User user, 
    		List<String> roles, 
    		String maskedAccountNumber, 
    		String maskedRrn 
    ) { 
    	EmployeeDetailResponseDto response = from(user, roles); 
    	
    	response.accountNumber = maskedAccountNumber; 
    	response.rrn = maskedRrn; 
    	
    	return response; 
    }
}