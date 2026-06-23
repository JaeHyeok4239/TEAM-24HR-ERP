package com.hr24.evaluation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.evaluation.entity.EmployeeEvaluationAnswer;

public interface EmployeeEvaluationAnswerRepository
        extends JpaRepository<EmployeeEvaluationAnswer, Long> {

    List<EmployeeEvaluationAnswer> findAllByEmployeeEvaluation_EmployeeEvaluationId(
            Long employeeEvaluationId
    );

    Optional<EmployeeEvaluationAnswer>
    findByEmployeeEvaluation_EmployeeEvaluationIdAndEvaluationQuestion_EvaluationQuestionId(
            Long employeeEvaluationId,
            Long evaluationQuestionId
    );
}