package com.hr24.evaluation.dto;

import java.time.LocalDateTime;

import com.hr24.evaluation.entity.EmployeeEvaluation;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmployeeEvaluationHistoryResponseDto {

    private Long employeeEvaluationId;
    private Long evaluationPeriodId;
    private String periodName;
    private Integer evaluationYear;
    private String halfType;
    private Integer totalScore;
    private String status;
    private String evaluatorName;
    private String comment;
    private LocalDateTime confirmedAt;

    public static EmployeeEvaluationHistoryResponseDto from(
            EmployeeEvaluation evaluation
    ) {
        EmployeeEvaluationHistoryResponseDto response =
                new EmployeeEvaluationHistoryResponseDto();

        response.employeeEvaluationId = evaluation.getEmployeeEvaluationId();

        if (evaluation.getEvaluationPeriod() != null) {
            response.evaluationPeriodId =
                    evaluation.getEvaluationPeriod().getEvaluationPeriodId();
            response.periodName = evaluation.getEvaluationPeriod().getPeriodName();
            response.evaluationYear =
                    evaluation.getEvaluationPeriod().getEvaluationYear();
            response.halfType = evaluation.getEvaluationPeriod().getHalfType() != null
                    ? evaluation.getEvaluationPeriod().getHalfType().name()
                    : null;
        }

        response.totalScore = evaluation.getTotalScore();
        response.status = evaluation.getStatus() != null
                ? evaluation.getStatus().name()
                : null;

        if (evaluation.getEvaluator() != null) {
            response.evaluatorName = evaluation.getEvaluator().getName();
        }

        response.comment = evaluation.getComment();
        response.confirmedAt = evaluation.getConfirmedAt();

        return response;
    }
}