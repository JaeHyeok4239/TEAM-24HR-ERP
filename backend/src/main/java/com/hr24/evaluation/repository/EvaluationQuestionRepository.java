package com.hr24.evaluation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.evaluation.entity.EvaluationQuestion;

public interface EvaluationQuestionRepository extends JpaRepository<EvaluationQuestion, Long> {

    List<EvaluationQuestion> findAllByIsActiveOrderBySortOrderAscEvaluationQuestionIdAsc(
            String isActive
    );
}