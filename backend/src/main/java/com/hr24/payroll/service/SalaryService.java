package com.hr24.payroll.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.payroll.dto.SalaryCreateRequestDto;
import com.hr24.payroll.dto.SalaryResponseDto;
import com.hr24.payroll.dto.SalaryUpdateRequestDto;
import com.hr24.payroll.entity.Salary;
import com.hr24.payroll.repository.SalaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final UserRepository userRepository;
    
    public Long createSalary(SalaryCreateRequestDto request) {

        User user = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("직원을 찾을 수 없습니다."));
        
        salaryRepository
        .findByEmployeeEmployeeId(request.getEmployeeId())
        .ifPresent(s -> {throw new RuntimeException("이미 기본급이 등록된 직원입니다.");});

        Salary salary = new Salary();

        salary.setEmployee(user);

        salary.setBaseSalary(request.getBaseSalary());

        salary.setCreatedAt(LocalDateTime.now());

        salaryRepository.save(salary);

        return salary.getSalaryId();
    }
    
    
    
    public void updateSalary(Long employeeId, SalaryUpdateRequestDto request) {

        Salary salary = salaryRepository
                        .findByEmployeeEmployeeId(employeeId)
                        .orElseThrow(() -> new RuntimeException("기본급 정보가 존재하지 않습니다."));

        salary.setBaseSalary(request.getBaseSalary());

        salary.setUpdatedAt(LocalDateTime.now());
    }
    
    
    public List<SalaryResponseDto> getSalaryList() {return salaryRepository.findSalaryList();}
}