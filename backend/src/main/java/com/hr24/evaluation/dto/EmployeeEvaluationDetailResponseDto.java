package com.hr24.evaluation.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hr24.evaluation.entity.EmployeeEvaluation;
import com.hr24.evaluation.entity.EmployeeEvaluationAnswer;
import com.hr24.evaluation.entity.EvaluationQuestion;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmployeeEvaluationDetailResponseDto {

    private Long employeeEvaluationId;
    private Long evaluationPeriodId;
    private String periodName;

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
    private String comment;
    private LocalDateTime evaluatedAt;
    private LocalDateTime confirmedAt;

    private List<AnswerDto> answers;

    public static EmployeeEvaluationDetailResponseDto from(
            EmployeeEvaluation evaluation,
            List<EvaluationQuestion> questions,
            List<EmployeeEvaluationAnswer> savedAnswers
    ) {
        EmployeeEvaluationDetailResponseDto response =
                new EmployeeEvaluationDetailResponseDto();

        response.employeeEvaluationId = evaluation.getEmployeeEvaluationId();

        if (evaluation.getEvaluationPeriod() != null) {
            response.evaluationPeriodId =
                    evaluation.getEvaluationPeriod().getEvaluationPeriodId();
            response.periodName = evaluation.getEvaluationPeriod().getPeriodName();
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
        response.comment = evaluation.getComment();
        response.evaluatedAt = evaluation.getEvaluatedAt();
        response.confirmedAt = evaluation.getConfirmedAt();

        Map<Long, Integer> scoreMap = savedAnswers.stream()
                .collect(Collectors.toMap(
                        answer -> answer.getEvaluationQuestion()
                                .getEvaluationQuestionId(),
                        EmployeeEvaluationAnswer::getScore
                ));

        response.answers = questions.stream()
                .map(question -> AnswerDto.of(
                        question,
                        scoreMap.get(question.getEvaluationQuestionId())
                ))
                .toList();

        return response;
    }

    @Getter
    @NoArgsConstructor
    public static class AnswerDto {

        private Long evaluationQuestionId;
        private String questionContent;
        private Integer maxScore;
        private Integer score;

        public static AnswerDto of(
                EvaluationQuestion question,
                Integer score
        ) {
            AnswerDto response = new AnswerDto();
            response.evaluationQuestionId = question.getEvaluationQuestionId();
            response.questionContent = question.getQuestionContent();
            response.maxScore = question.getMaxScore();
            response.score = score;
            return response;
        }
    }
}