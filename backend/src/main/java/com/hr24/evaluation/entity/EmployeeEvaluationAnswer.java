package com.hr24.evaluation.entity;

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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "employee_evaluation_answers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_employee_eval_answer_question",
                        columnNames = {"employee_evaluation_id", "evaluation_question_id"}
                )
        }
)
public class EmployeeEvaluationAnswer extends BaseTimeEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "evaluation_answer_generator"
    )
    @SequenceGenerator(
            name = "evaluation_answer_generator",
            sequenceName = "evaluation_answer_seq",
            allocationSize = 1
    )
    @Column(name = "evaluation_answer_id")
    private Long evaluationAnswerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_evaluation_id", nullable = false)
    private EmployeeEvaluation employeeEvaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_question_id", nullable = false)
    private EvaluationQuestion evaluationQuestion;

    @Column(name = "score", nullable = false)
    private Integer score;

    public static EmployeeEvaluationAnswer create(
            EmployeeEvaluation employeeEvaluation,
            EvaluationQuestion evaluationQuestion,
            Integer score
    ) {
        EmployeeEvaluationAnswer answer = new EmployeeEvaluationAnswer();
        answer.employeeEvaluation = employeeEvaluation;
        answer.evaluationQuestion = evaluationQuestion;
        answer.score = score;
        return answer;
    }

    public void updateScore(Integer score) {
        this.score = score;
    }
}