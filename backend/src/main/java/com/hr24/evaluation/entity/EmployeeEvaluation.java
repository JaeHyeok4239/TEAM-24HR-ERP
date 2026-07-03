package com.hr24.evaluation.entity;

import java.time.LocalDateTime;

import com.hr24.employee.entity.Department;
import com.hr24.employee.entity.Position;
import com.hr24.employee.entity.User;
import com.hr24.evaluation.enums.EmployeeEvaluationStatus;
import com.hr24.evaluation.enums.EvaluationGrade;
import com.hr24.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "employee_evaluations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_employee_evaluation_period_employee",
                        columnNames = {"evaluation_period_id", "employee_id"}
                )
        }
)
public class EmployeeEvaluation extends BaseTimeEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "employee_evaluation_generator"
    )
    @SequenceGenerator(
            name = "employee_evaluation_generator",
            sequenceName = "employee_evaluation_seq",
            allocationSize = 1
    )
    @Column(name = "employee_evaluation_id")
    private Long employeeEvaluationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_period_id", nullable = false)
    private EvaluationPeriod evaluationPeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id")
    private User evaluator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    @Column(name = "total_score")
    private Integer totalScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade", length = 5)
    private EvaluationGrade grade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EmployeeEvaluationStatus status = EmployeeEvaluationStatus.PENDING;

    @Column(name = "comments", length = 1000)
    private String comment;

    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    public static EmployeeEvaluation create(
            EvaluationPeriod evaluationPeriod,
            User employee,
            Department department,
            Position position
    ) {
        EmployeeEvaluation evaluation = new EmployeeEvaluation();
        evaluation.evaluationPeriod = evaluationPeriod;
        evaluation.employee = employee;
        evaluation.department = department;
        evaluation.position = position;
        evaluation.status = EmployeeEvaluationStatus.PENDING;
        return evaluation;
    }

    public void save(
            User evaluator,
            Integer totalScore,
            String comment
    ) {
        this.evaluator = evaluator;
        this.totalScore = totalScore;
        this.comment = comment;
        this.status = EmployeeEvaluationStatus.SAVED;
        this.evaluatedAt = LocalDateTime.now();
    }

    public void confirm(
            User evaluator,
            Integer totalScore,
            String comment
    ) {
        this.evaluator = evaluator;
        this.totalScore = totalScore;
        this.grade = totalScore != null ? EvaluationGrade.from(totalScore) : null;
        this.comment = comment;
        this.status = EmployeeEvaluationStatus.CONFIRMED;
        this.evaluatedAt = LocalDateTime.now();
        this.confirmedAt = LocalDateTime.now();
    }
}