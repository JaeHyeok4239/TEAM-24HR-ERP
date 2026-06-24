package com.hr24.evaluation.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmployeeEvaluationSaveRequestDto {

    @NotEmpty(message = "평가 답변은 필수입니다.")
    private List<@Valid EmployeeEvaluationAnswerRequestDto> answers;

    private String comment;
}