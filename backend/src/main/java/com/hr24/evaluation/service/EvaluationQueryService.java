package com.hr24.evaluation.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.entity.User;
import com.hr24.employee.enums.EmploymentType;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.repository.UserRepository;
import com.hr24.evaluation.dto.EmployeeEvaluationDetailResponseDto;
import com.hr24.evaluation.dto.EmployeeEvaluationHistoryResponseDto;
import com.hr24.evaluation.dto.EmployeeEvaluationTargetResponseDto;
import com.hr24.evaluation.dto.EvaluationPeriodResponseDto;
import com.hr24.evaluation.dto.EvaluationResultSummaryResponseDto;
import com.hr24.evaluation.dto.PromotionCandidateResponseDto;
import com.hr24.evaluation.entity.EmployeeEvaluation;
import com.hr24.evaluation.entity.EmployeeEvaluationAnswer;
import com.hr24.evaluation.entity.EvaluationQuestion;
import com.hr24.evaluation.enums.EmployeeEvaluationStatus;
import com.hr24.evaluation.enums.EvaluationGrade;
import com.hr24.evaluation.repository.EmployeeEvaluationAnswerRepository;
import com.hr24.evaluation.repository.EmployeeEvaluationRepository;
import com.hr24.evaluation.repository.EvaluationPeriodRepository;
import com.hr24.evaluation.repository.EvaluationQuestionRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvaluationQueryService {

    private final EvaluationPeriodRepository evaluationPeriodRepository;
    private final EvaluationQuestionRepository evaluationQuestionRepository;
    private final EmployeeEvaluationRepository employeeEvaluationRepository;
    private final EmployeeEvaluationAnswerRepository employeeEvaluationAnswerRepository;
    private final UserRepository userRepository;

    public List<EvaluationPeriodResponseDto> findPeriods() {
        return evaluationPeriodRepository
                .findAllByOrderByEvaluationYearDescEvaluationPeriodIdDesc()
                .stream()
                .map(EvaluationPeriodResponseDto::from)
                .toList();
    }

    public List<EmployeeEvaluationTargetResponseDto> findTargets(Long periodId) {
        return employeeEvaluationRepository
                .findAllByPeriodIdWithDetails(periodId)
                .stream()
                .map(EmployeeEvaluationTargetResponseDto::from)
                .toList();
    }

    public EmployeeEvaluationDetailResponseDto findEvaluationDetail(
            Long employeeEvaluationId
    ) {
        EmployeeEvaluation evaluation =
                employeeEvaluationRepository.findByIdWithDetails(employeeEvaluationId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND));

        List<EvaluationQuestion> questions =
                evaluationQuestionRepository
                        .findAllByIsActiveOrderBySortOrderAscEvaluationQuestionIdAsc("Y");

        List<EmployeeEvaluationAnswer> answers =
                employeeEvaluationAnswerRepository
                        .findAllByEmployeeEvaluation_EmployeeEvaluationId(
                                employeeEvaluationId
                        );

        return EmployeeEvaluationDetailResponseDto.from(evaluation, questions, answers);
    }

    public List<EvaluationResultSummaryResponseDto> findResultSummaries() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getStatus() == UserStatus.ACTIVE)
                .filter(user -> user.getEmploymentType() == EmploymentType.REGULAR)
                .map(this::toResultSummary)
                .toList();
    }

    public List<EmployeeEvaluationHistoryResponseDto> findEmployeeHistories(
            Long employeeId
    ) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return employeeEvaluationRepository
                .findByEmployeeIdAndStatusWithDetails(
                        employee.getEmployeeId(),
                        EmployeeEvaluationStatus.CONFIRMED
                )
                .stream()
                .map(EmployeeEvaluationHistoryResponseDto::from)
                .toList();
    }

    public List<PromotionCandidateResponseDto> findPromotionCandidates() {
        return findResultSummaries()
                .stream()
                .filter(summary -> Boolean.TRUE.equals(summary.getPromotionCandidate()))
                .map(PromotionCandidateResponseDto::from)
                .toList();
    }

    private EvaluationResultSummaryResponseDto toResultSummary(User employee) {
        List<EmployeeEvaluation> confirmedEvaluations =
                employeeEvaluationRepository
                        .findByEmployeeIdAndStatusWithDetails(
                                employee.getEmployeeId(),
                                EmployeeEvaluationStatus.CONFIRMED
                        );

        int evaluationCount = confirmedEvaluations.size();

        int totalScore = confirmedEvaluations.stream()
                .mapToInt(e -> e.getTotalScore() != null ? e.getTotalScore() : 0)
                .sum();

        Integer latestScore = confirmedEvaluations.isEmpty()
                ? null
                : confirmedEvaluations.get(0).getTotalScore();

        EvaluationGrade latestGrade = confirmedEvaluations.isEmpty()
                ? null
                : confirmedEvaluations.get(0).getGrade();

        // 연차 계산 (입사일 기준)
        long yearsOfService = 0;
        if (employee.getHireDate() != null) {
            yearsOfService = ChronoUnit.YEARS.between(
                    employee.getHireDate().toLocalDate(),
                    LocalDate.now()
            );
        }

        // 진급 대상자 판단
        // 3년 이상: 일반 진급 대상자
        // 2년 이상 + 최근 4번 중 S 3번 이상: 조기 진급 대상자
        String promotionType = null;
        int sGradeCount = 0;

        if (yearsOfService >= 3) {
            promotionType = "REGULAR";
        } else if (yearsOfService >= 2) {
            List<EmployeeEvaluation> recent4 = confirmedEvaluations.stream()
                    .limit(4)
                    .toList();
            sGradeCount = (int) recent4.stream()
                    .filter(e -> e.getGrade() == EvaluationGrade.S)
                    .count();
            if (sGradeCount >= 3) {
                promotionType = "EARLY";
            }
        }

        return EvaluationResultSummaryResponseDto.of(
                employee,
                evaluationCount,
                totalScore,
                latestScore,
                latestGrade,
                yearsOfService,
                promotionType,
                sGradeCount
        );
    }
}
