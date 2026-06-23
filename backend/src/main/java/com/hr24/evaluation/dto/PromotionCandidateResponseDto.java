package com.hr24.evaluation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PromotionCandidateResponseDto {

    private Long employeeId;
    private String employeeNo;
    private String employeeName;
    private String departmentName;
    private Long currentPositionId;
    private String currentPositionName;
    private Long targetPositionId;
    private String targetPositionName;
    private Integer totalScore;
    private Integer requiredScore;
    private Integer evaluationCount;
    private Integer minEvaluationCount;

    public static PromotionCandidateResponseDto from(
            EvaluationResultSummaryResponseDto summary
    ) {
        PromotionCandidateResponseDto response =
                new PromotionCandidateResponseDto();

        response.employeeId = summary.getEmployeeId();
        response.employeeNo = summary.getEmployeeNo();
        response.employeeName = summary.getEmployeeName();
        response.departmentName = summary.getDepartmentName();
        response.currentPositionId = summary.getPositionId();
        response.currentPositionName = summary.getPositionName();
        response.targetPositionId = summary.getTargetPositionId();
        response.targetPositionName = summary.getTargetPositionName();
        response.totalScore = summary.getTotalScore();
        response.requiredScore = summary.getRequiredScore();
        response.evaluationCount = summary.getEvaluationCount();
        response.minEvaluationCount = summary.getMinEvaluationCount();

        return response;
    }
}