package com.hr24.payroll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.attendance.repository.AttendanceResultRepository;
import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.payroll.dto.PayrollCalculateRequestDto;
import com.hr24.payroll.dto.PayrollResponseDto;
import com.hr24.payroll.entity.Payroll;
import com.hr24.payroll.entity.Salary;
import com.hr24.payroll.repository.PayrollRepository;
import com.hr24.payroll.repository.SalaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollCalculateService {
	
	private final PayrollRepository payrollRepository;
	private final UserRepository userRepository;
	private final SalaryRepository salaryRepository;
	private final AttendanceResultRepository attendanceResultRepository;
	
	@Transactional
	public PayrollResponseDto calculatePayroll(PayrollCalculateRequestDto request) {

	    User user = userRepository.findById(request.getEmployeeId())
	    		.orElseThrow(() -> new RuntimeException("직원 없음"));

	    Salary salary = salaryRepository
	            .findByEmployeeEmployeeId(request.getEmployeeId())
	            .orElseThrow(() -> new RuntimeException("기본급 정보 없음"));

	    BigDecimal baseSalary = salary.getBaseSalary();

	    // 직책수당

	    BigDecimal positionAllowance = calculatePositionAllowance(
	                    user.getPosition()
	                        .getPositionId()
	            );

	    // 식대

	    BigDecimal mealAllowance = new BigDecimal("200000");

	    // 교통비

	    BigDecimal transportAllowance = new BigDecimal("100000");

	    // 초과근무수당

	    BigDecimal overtimePay = calculateOvertimePay(request.getEmployeeId());

	    // 총지급액

	    BigDecimal totalPay = baseSalary
	                    .add(positionAllowance)
	                    .add(mealAllowance)
	                    .add(transportAllowance)
	                    .add(overtimePay);

	    // 국민연금

	    BigDecimal nationalPension = totalPay.multiply(new BigDecimal("0.045"))
	    		                             .setScale(0, RoundingMode.HALF_UP);

	    // 건강보험
	    
	    BigDecimal healthInsurance = totalPay.multiply(new BigDecimal("0.03545"))
	    		                             .setScale(0, RoundingMode.HALF_UP);

	    // 고용보험
	    
	    BigDecimal employmentInsurance = totalPay.multiply(new BigDecimal("0.009"))
	    		                                 .setScale(0, RoundingMode.HALF_UP);

	    // 소득세
	    
	    BigDecimal incomeTax = calculateIncomeTax(totalPay);

	    // 지방소득세
	    
	    BigDecimal localIncomeTax = incomeTax.multiply(new BigDecimal("0.1"))
	    		                             .setScale(0, RoundingMode.HALF_UP);

	    BigDecimal totalDeduction = nationalPension
	                    .add(healthInsurance)
	                    .add(employmentInsurance)
	                    .add(incomeTax)
	                    .add(localIncomeTax);

	    BigDecimal netSalary = totalPay.subtract(totalDeduction);

	    Payroll payroll = Payroll
	    				.builder()
	                    .user(user)
	                    .payMonth(request.getPayMonth())
	                    .totalPay(totalPay)
	                    .totalDeduction(totalDeduction)
	                    .netSalary(netSalary)
	                    .status("CONFIRMED")
	                    .createdAt(LocalDateTime.now())
	                    .build();

	    payrollRepository.save(payroll);

	    return toDto(payroll);
	}
	
	
	private PayrollResponseDto toDto(Payroll payroll) {

        return PayrollResponseDto.builder()
                .payrollId(payroll.getPayrollId())
                .employeeNo(payroll.getUser().getEmployeeNo())
                .employeeName(payroll.getUser().getName())
                .departmentName(
                        payroll.getUser()
                                .getDepartment()
                                .getDepartmentName()
                )
                .payMonth(payroll.getPayMonth())
                .totalPay(payroll.getTotalPay())
                .totalDeduction(payroll.getTotalDeduction())
                .netSalary(payroll.getNetSalary())
                .status(payroll.getStatus())
                .build();
    }
	
	
	private BigDecimal calculatePositionAllowance(Long positionId) {

		return switch (positionId.intValue()) {	    
	    	case 4 -> new BigDecimal("200000");
	    	case 5 -> new BigDecimal("300000");
	    	case 6 -> new BigDecimal("500000");
	    	case 7 -> new BigDecimal("800000");
	    	case 8 -> new BigDecimal("1000000");
	    	default -> BigDecimal.ZERO;
		};
	}
	
	
	private BigDecimal calculateOvertimePay(Long employeeId) {

	    List<AttendanceResult> results = attendanceResultRepository
	    		.findByEmployeeEmployeeId(employeeId);

	    long totalMinutes = results
	    			   .stream()
	                   .mapToLong(AttendanceResult::getOvertimeMinutes)
	                   .sum();

	    BigDecimal overtimeHours = BigDecimal.valueOf(totalMinutes)
	                    .divide(new BigDecimal("60"), 2, RoundingMode.HALF_UP);

	    return overtimeHours.multiply(new BigDecimal("15000"));
	}
	
	
	private BigDecimal calculateIncomeTax(BigDecimal totalPay) {
		
	    if(totalPay.compareTo(new BigDecimal("3000000")) <= 0) {
	        return totalPay.multiply(new BigDecimal("0.03"))
	        		       .setScale(0, RoundingMode.HALF_UP);
	    }

	    if(totalPay.compareTo(new BigDecimal("5000000")) <= 0) {
	        return totalPay.multiply(new BigDecimal("0.05"))
	        		       .setScale(0, RoundingMode.HALF_UP);
	    }

	    return totalPay.multiply(new BigDecimal("0.07"))
	    		       .setScale(0, RoundingMode.HALF_UP);
	}
}
