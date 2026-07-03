package com.hr24.evaluation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmployeeEvaluationAnswerRequestDto {

    @NotNull(message = "평가 문항 ID는 필수입니다.")
    private Long evaluationQuestionId;

    @NotNull(message = "평가 점수는 필수입니다.")
    @Min(value = 1, message = "평가 점수는 최소 1점입니다.")
    @Max(value = 5, message = "평가 점수는 최대 5점입니다.")
    private Integer score;
}