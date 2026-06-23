package com.hr24.evaluation.dto;

import java.time.LocalDateTime;

import com.hr24.evaluation.entity.EmployeeEvaluation;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmployeeEvaluationTargetResponseDto {

    private Long employeeEvaluationId;
    private Long evaluationPeriodId;

    private Long employeeId;
    private String employeeNo;
    private String employeeName;

    private Long departmentId;
    private String departmentName;

    private Long positionId;
    private String positionName;

    private Long evaluatorId;
    private String evaluatorName;

    private Integer totalScore;
    private String status;
    private LocalDateTime evaluatedAt;
    private LocalDateTime confirmedAt;

    public static EmployeeEvaluationTargetResponseDto from(
            EmployeeEvaluation evaluation
    ) {
        EmployeeEvaluationTargetResponseDto response =
                new EmployeeEvaluationTargetResponseDto();

        response.employeeEvaluationId = evaluation.getEmployeeEvaluationId();

        if (evaluation.getEvaluationPeriod() != null) {
            response.evaluationPeriodId =
                    evaluation.getEvaluationPeriod().getEvaluationPeriodId();
        }

        if (evaluation.getEmployee() != null) {
            response.employeeId = evaluation.getEmployee().getEmployeeId();
            response.employeeNo = evaluation.getEmployee().getEmployeeNo();
            response.employeeName = evaluation.getEmployee().getName();
        }

        if (evaluation.getDepartment() != null) {
            response.departmentId = evaluation.getDepartment().getDepartmentId();
            response.departmentName = evaluation.getDepartment().getDepartmentName();
        }

        if (evaluation.getPosition() != null) {
            response.positionId = evaluation.getPosition().getPositionId();
            response.positionName = evaluation.getPosition().getPositionName();
        }

        if (evaluation.getEvaluator() != null) {
            response.evaluatorId = evaluation.getEvaluator().getEmployeeId();
            response.evaluatorName = evaluation.getEvaluator().getName();
        }

        response.totalScore = evaluation.getTotalScore();
        response.status = evaluation.getStatus() != null
                ? evaluation.getStatus().name()
                : null;
        response.evaluatedAt = evaluation.getEvaluatedAt();
        response.confirmedAt = evaluation.getConfirmedAt();

        return response;
    }
}