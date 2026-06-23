package com.hr24.evaluation.dto;

import java.time.LocalDate;

import com.hr24.evaluation.enums.EvaluationHalfType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EvaluationPeriodCreateRequestDto {

    @NotNull(message = "평가연도는 필수입니다.")
    @Min(value = 2000, message = "평가연도가 올바르지 않습니다.")
    private Integer evaluationYear;

    @NotNull(message = "반기 구분은 필수입니다.")
    private EvaluationHalfType halfType;

    private String periodName;

    @NotNull(message = "평가 대상 시작일은 필수입니다.")
    private LocalDate targetStartDate;

    @NotNull(message = "평가 대상 종료일은 필수입니다.")
    private LocalDate targetEndDate;

    @NotNull(message = "평가 입력 시작일은 필수입니다.")
    private LocalDate inputStartDate;

    @NotNull(message = "평가 입력 종료일은 필수입니다.")
    private LocalDate inputEndDate;
}