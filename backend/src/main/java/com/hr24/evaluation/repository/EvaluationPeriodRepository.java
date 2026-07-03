package com.hr24.evaluation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.evaluation.entity.EvaluationPeriod;
import com.hr24.evaluation.enums.EvaluationHalfType;

public interface EvaluationPeriodRepository extends JpaRepository<EvaluationPeriod, Long> {

    boolean existsByEvaluationYearAndHalfType(
            Integer evaluationYear,
            EvaluationHalfType halfType
    );

    List<EvaluationPeriod> findAllByOrderByEvaluationYearDescEvaluationPeriodIdDesc();
}