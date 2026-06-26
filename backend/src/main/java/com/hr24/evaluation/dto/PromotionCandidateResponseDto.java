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
    private String currentPositionName;
    private Integer evaluationCount;
    private Integer totalScore;
    private String latestGrade;
    private Long yearsOfService;
    private String promotionType;  // EARLY(조기) or REGULAR(일반)
    private Integer sGradeCount;

    public static PromotionCandidateResponseDto from(
            EvaluationResultSummaryResponseDto summary
    ) {
        PromotionCandidateResponseDto response =
                new PromotionCandidateResponseDto();

        response.employeeId = summary.getEmployeeId();
        response.employeeNo = summary.getEmployeeNo();
        response.employeeName = summary.getEmployeeName();
        response.departmentName = summary.getDepartmentName();
        response.currentPositionName = summary.getPositionName();
        response.evaluationCount = summary.getEvaluationCount();
        response.totalScore = summary.getTotalScore();
        response.latestGrade = summary.getLatestGrade();
        response.yearsOfService = summary.getYearsOfService();
        response.promotionType = summary.getPromotionType();
        response.sGradeCount = summary.getSGradeCount();

        return response;
    }
}
