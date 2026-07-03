package com.hr24.payroll.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hr24.payroll.dto.DepartmentCostDto;
import com.hr24.payroll.dto.MonthlyCostDto;
import com.hr24.payroll.repository.PayrollRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PayrollRepository payrollRepository;

    public List<MonthlyCostDto> getMonthlyCost() {

        return payrollRepository.getMonthlyCost();
    }
    
    
    public List<DepartmentCostDto> getDepartmentCost(String month) {

        return payrollRepository.getDepartmentCost(month);
    }
}