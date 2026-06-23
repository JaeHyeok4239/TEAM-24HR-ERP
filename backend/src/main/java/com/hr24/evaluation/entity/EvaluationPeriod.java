package com.hr24.evaluation.entity;

import java.time.LocalDate;

import com.hr24.evaluation.enums.EvaluationHalfType;
import com.hr24.evaluation.enums.EvaluationPeriodStatus;
import com.hr24.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "evaluation_periods",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_evaluation_period_year_half",
                        columnNames = {"evaluation_year", "half_type"}
                )
        }
)
public class EvaluationPeriod extends BaseTimeEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "evaluation_period_generator"
    )
    @SequenceGenerator(
            name = "evaluation_period_generator",
            sequenceName = "evaluation_period_seq",
            allocationSize = 1
    )
    @Column(name = "evaluation_period_id")
    private Long evaluationPeriodId;

    @Column(name = "evaluation_year", nullable = false)
    private Integer evaluationYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "half_type", nullable = false, length = 20)
    private EvaluationHalfType halfType;

    @Column(name = "period_name", nullable = false, length = 100)
    private String periodName;

    @Column(name = "target_start_date", nullable = false)
    private LocalDate targetStartDate;

    @Column(name = "target_end_date", nullable = false)
    private LocalDate targetEndDate;

    @Column(name = "input_start_date", nullable = false)
    private LocalDate inputStartDate;

    @Column(name = "input_end_date", nullable = false)
    private LocalDate inputEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EvaluationPeriodStatus status = EvaluationPeriodStatus.DRAFT;

    public static EvaluationPeriod create(
            Integer evaluationYear,
            EvaluationHalfType halfType,
            String periodName,
            LocalDate targetStartDate,
            LocalDate targetEndDate,
            LocalDate inputStartDate,
            LocalDate inputEndDate
    ) {
        EvaluationPeriod period = new EvaluationPeriod();
        period.evaluationYear = evaluationYear;
        period.halfType = halfType;
        period.periodName = periodName;
        period.targetStartDate = targetStartDate;
        period.targetEndDate = targetEndDate;
        period.inputStartDate = inputStartDate;
        period.inputEndDate = inputEndDate;
        period.status = EvaluationPeriodStatus.DRAFT;
        return period;
    }

    public void open() {
        this.status = EvaluationPeriodStatus.OPEN;
    }

    public void close() {
        this.status = EvaluationPeriodStatus.CLOSED;
    }
}