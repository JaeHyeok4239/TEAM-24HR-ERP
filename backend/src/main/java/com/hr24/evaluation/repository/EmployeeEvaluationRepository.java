package com.hr24.evaluation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr24.evaluation.entity.EmployeeEvaluation;
import com.hr24.evaluation.enums.EmployeeEvaluationStatus;

public interface EmployeeEvaluationRepository
        extends JpaRepository<EmployeeEvaluation, Long> {

    boolean existsByEvaluationPeriod_EvaluationPeriodIdAndEmployee_EmployeeId(
            Long evaluationPeriodId,
            Long employeeId
    );

    @Query("""
            select ee
            from EmployeeEvaluation ee
            join fetch ee.evaluationPeriod ep
            join fetch ee.employee employee
            left join fetch ee.evaluator evaluator
            left join fetch ee.department department
            left join fetch ee.position position
            where ep.evaluationPeriodId = :periodId
            order by employee.name asc, ee.employeeEvaluationId asc
            """)
    List<EmployeeEvaluation> findAllByPeriodIdWithDetails(
            @Param("periodId") Long periodId
    );

    @Query("""
            select ee
            from EmployeeEvaluation ee
            join fetch ee.evaluationPeriod ep
            join fetch ee.employee employee
            left join fetch ee.evaluator evaluator
            left join fetch ee.department department
            left join fetch ee.position position
            where ee.employeeEvaluationId = :employeeEvaluationId
            """)
    Optional<EmployeeEvaluation> findByIdWithDetails(
            @Param("employeeEvaluationId") Long employeeEvaluationId
    );

    @Query("""
            select ee
            from EmployeeEvaluation ee
            join fetch ee.evaluationPeriod ep
            join fetch ee.employee employee
            left join fetch ee.evaluator evaluator
            left join fetch ee.department department
            left join fetch ee.position position
            where employee.employeeId = :employeeId
              and ee.status = :status
            order by ep.evaluationYear desc, ep.halfType desc
            """)
    List<EmployeeEvaluation> findByEmployeeIdAndStatusWithDetails(
            @Param("employeeId") Long employeeId,
            @Param("status") EmployeeEvaluationStatus status
    );
}