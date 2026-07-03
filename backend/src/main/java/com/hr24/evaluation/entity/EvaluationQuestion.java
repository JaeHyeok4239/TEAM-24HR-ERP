package com.hr24.evaluation.entity;

import com.hr24.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "evaluation_questions")
public class EvaluationQuestion extends BaseTimeEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "evaluation_question_generator"
    )
    @SequenceGenerator(
            name = "evaluation_question_generator",
            sequenceName = "evaluation_question_seq",
            allocationSize = 1
    )
    @Column(name = "evaluation_question_id")
    private Long evaluationQuestionId;

    @Column(name = "question_content", nullable = false, length = 500)
    private String questionContent;

    @Column(name = "max_score", nullable = false)
    private Integer maxScore = 5;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false, length = 1)
    private String isActive = "Y";
}