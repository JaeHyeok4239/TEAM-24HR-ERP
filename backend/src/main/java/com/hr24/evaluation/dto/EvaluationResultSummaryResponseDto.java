package com.hr24.evaluation.dto;

import com.hr24.employee.entity.User;
import com.hr24.evaluation.enums.EvaluationGrade;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EvaluationResultSummaryResponseDto {

    private Long employeeId;
    private String employeeNo;
    private String employeeName;

    private Long departmentId;
    private String departmentName;

    private Long positionId;
    private String positionName;

    private Integer evaluationCount;
    private Integer totalScore;
    private Integer latestScore;
    private String latestGrade;

    private Long yearsOfService;
    private Boolean promotionCandidate;
    private String promotionType;  // EARLY(조기) or REGULAR(일반)
    private Integer sGradeCount;

    public static EvaluationResultSummaryResponseDto of(
            User employee,
            Integer evaluationCount,
            Integer totalScore,
            Integer latestScore,
            EvaluationGrade latestGrade,
            Long yearsOfService,
            String promotionType,
            Integer sGradeCount
    ) {
        EvaluationResultSummaryResponseDto response =
                new EvaluationResultSummaryResponseDto();

        response.employeeId = employee.getEmployeeId();
        response.employeeNo = employee.getEmployeeNo();
        response.employeeName = employee.getName();

        if (employee.getDepartment() != null) {
            response.departmentId = employee.getDepartment().getDepartmentId();
            response.departmentName = employee.getDepartment().getDepartmentName();
        }

        if (employee.getPosition() != null) {
            response.positionId = employee.getPosition().getPositionId();
            response.positionName = employee.getPosition().getPositionName();
        }

        response.evaluationCount = evaluationCount;
        response.totalScore = totalScore;
        response.latestScore = latestScore;
        response.latestGrade = latestGrade != null ? latestGrade.name() : null;
        response.yearsOfService = yearsOfService;
        response.promotionType = promotionType;
        response.promotionCandidate = promotionType != null;
        response.sGradeCount = sGradeCount;

        return response;
    }
}
