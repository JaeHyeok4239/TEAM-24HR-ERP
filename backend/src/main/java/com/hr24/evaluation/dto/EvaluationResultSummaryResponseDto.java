package com.hr24.evaluation.dto;

import com.hr24.employee.entity.User;

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

    private Boolean promotionCandidate;
    private Long targetPositionId;
    private String targetPositionName;
    private Integer requiredScore;
    private Integer minEvaluationCount;

    public static EvaluationResultSummaryResponseDto of(
            User employee,
            Integer evaluationCount,
            Integer totalScore,
            Integer latestScore,
            Boolean promotionCandidate,
            Long targetPositionId,
            String targetPositionName,
            Integer requiredScore,
            Integer minEvaluationCount
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
        response.promotionCandidate = promotionCandidate;
        response.targetPositionId = targetPositionId;
        response.targetPositionName = targetPositionName;
        response.requiredScore = requiredScore;
        response.minEvaluationCount = minEvaluationCount;

        return response;
    }
}