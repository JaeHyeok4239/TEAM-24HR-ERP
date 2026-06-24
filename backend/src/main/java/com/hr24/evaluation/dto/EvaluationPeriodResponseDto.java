package com.hr24.evaluation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hr24.evaluation.entity.EvaluationPeriod;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EvaluationPeriodResponseDto {

    private Long evaluationPeriodId;
    private Integer evaluationYear;
    private String halfType;
    private String halfTypeLabel;
    private String periodName;
    private LocalDate targetStartDate;
    private LocalDate targetEndDate;
    private LocalDate inputStartDate;
    private LocalDate inputEndDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EvaluationPeriodResponseDto from(EvaluationPeriod period) {
        EvaluationPeriodResponseDto response = new EvaluationPeriodResponseDto();

        response.evaluationPeriodId = period.getEvaluationPeriodId();
        response.evaluationYear = period.getEvaluationYear();
        response.halfType = period.getHalfType() != null
                ? period.getHalfType().name()
                : null;
        response.halfTypeLabel = period.getHalfType() != null
                ? period.getHalfType().getLabel()
                : null;
        response.periodName = period.getPeriodName();
        response.targetStartDate = period.getTargetStartDate();
        response.targetEndDate = period.getTargetEndDate();
        response.inputStartDate = period.getInputStartDate();
        response.inputEndDate = period.getInputEndDate();
        response.status = period.getStatus() != null
                ? period.getStatus().name()
                : null;
        response.createdAt = period.getCreatedAt();
        response.updatedAt = period.getUpdatedAt();

        return response;
    }
}