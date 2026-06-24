package com.hr24.evaluation.entity;

import com.hr24.employee.entity.Position;
import com.hr24.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "promotion_rules")
public class PromotionRule extends BaseTimeEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "promotion_rule_generator"
    )
    @SequenceGenerator(
            name = "promotion_rule_generator",
            sequenceName = "promotion_rule_seq",
            allocationSize = 1
    )
    @Column(name = "promotion_rule_id")
    private Long promotionRuleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_position_id", nullable = false)
    private Position fromPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_position_id", nullable = false)
    private Position toPosition;

    @Column(name = "required_score", nullable = false)
    private Integer requiredScore;

    @Column(name = "min_evaluation_count", nullable = false)
    private Integer minEvaluationCount = 2;

    @Column(name = "is_active", nullable = false, length = 1)
    private String isActive = "Y";
}