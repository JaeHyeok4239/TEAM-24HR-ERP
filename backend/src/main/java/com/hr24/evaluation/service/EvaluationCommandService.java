package com.hr24.evaluation.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.entity.User;
import com.hr24.employee.enums.EmploymentType;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.repository.UserRepository;
import com.hr24.evaluation.dto.EmployeeEvaluationDetailResponseDto;
import com.hr24.evaluation.dto.EmployeeEvaluationSaveRequestDto;
import com.hr24.evaluation.dto.EmployeeEvaluationTargetResponseDto;
import com.hr24.evaluation.dto.EvaluationPeriodCreateRequestDto;
import com.hr24.evaluation.dto.EvaluationPeriodResponseDto;
import com.hr24.evaluation.entity.EmployeeEvaluation;
import com.hr24.evaluation.entity.EmployeeEvaluationAnswer;
import com.hr24.evaluation.entity.EvaluationPeriod;
import com.hr24.evaluation.entity.EvaluationQuestion;
import com.hr24.evaluation.enums.EmployeeEvaluationStatus;
import com.hr24.evaluation.enums.EvaluationPeriodStatus;
import com.hr24.evaluation.repository.EmployeeEvaluationAnswerRepository;
import com.hr24.evaluation.repository.EmployeeEvaluationRepository;
import com.hr24.evaluation.repository.EvaluationPeriodRepository;
import com.hr24.evaluation.repository.EvaluationQuestionRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EvaluationCommandService {

    private final EvaluationPeriodRepository evaluationPeriodRepository;
    private final EvaluationQuestionRepository evaluationQuestionRepository;
    private final EmployeeEvaluationRepository employeeEvaluationRepository;
    private final EmployeeEvaluationAnswerRepository employeeEvaluationAnswerRepository;
    private final UserRepository userRepository;

    public EvaluationPeriodResponseDto createPeriod(
            EvaluationPeriodCreateRequestDto request
    ) {
        validateDateRange(request);

        if (evaluationPeriodRepository.existsByEvaluationYearAndHalfType(
                request.getEvaluationYear(),
                request.getHalfType()
        )) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        String periodName = normalizeNullable(request.getPeriodName());

        if (periodName == null) {
            periodName = request.getEvaluationYear()
                    + "년 "
                    + request.getHalfType().getLabel()
                    + " 인사평가";
        }

        EvaluationPeriod period = EvaluationPeriod.create(
                request.getEvaluationYear(),
                request.getHalfType(),
                periodName,
                request.getTargetStartDate(),
                request.getTargetEndDate(),
                request.getInputStartDate(),
                request.getInputEndDate()
        );

        EvaluationPeriod savedPeriod = evaluationPeriodRepository.save(period);

        return EvaluationPeriodResponseDto.from(savedPeriod);
    }

    public EvaluationPeriodResponseDto openPeriod(Long periodId) {
        EvaluationPeriod period = findPeriod(periodId);

        if (period.getStatus() == EvaluationPeriodStatus.CLOSED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        period.open();

        return EvaluationPeriodResponseDto.from(period);
    }

    public EvaluationPeriodResponseDto closePeriod(Long periodId) {
        EvaluationPeriod period = findPeriod(periodId);

        if (period.getStatus() == EvaluationPeriodStatus.DRAFT) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        period.close();

        return EvaluationPeriodResponseDto.from(period);
    }

    public List<EmployeeEvaluationTargetResponseDto> createTargets(
            Long periodId
    ) {
        EvaluationPeriod period = findPeriod(periodId);

        if (period.getStatus() == EvaluationPeriodStatus.CLOSED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        List<User> employees = userRepository.findAll()
                .stream()
                .filter(user -> user.getStatus() == UserStatus.ACTIVE)
                .filter(user -> user.getEmploymentType() == EmploymentType.REGULAR)
                .toList();

        for (User employee : employees) {
            boolean exists =
                    employeeEvaluationRepository
                            .existsByEvaluationPeriod_EvaluationPeriodIdAndEmployee_EmployeeId(
                                    period.getEvaluationPeriodId(),
                                    employee.getEmployeeId()
                            );

            if (exists) {
                continue;
            }

            EmployeeEvaluation evaluation = EmployeeEvaluation.create(
                    period,
                    employee,
                    employee.getDepartment(),
                    employee.getPosition()
            );

            employeeEvaluationRepository.save(evaluation);
        }

        return employeeEvaluationRepository
                .findAllByPeriodIdWithDetails(period.getEvaluationPeriodId())
                .stream()
                .map(EmployeeEvaluationTargetResponseDto::from)
                .toList();
    }

    public EmployeeEvaluationDetailResponseDto saveEvaluation(
            Long employeeEvaluationId,
            EmployeeEvaluationSaveRequestDto request
    ) {
        return updateEvaluation(employeeEvaluationId, request, false);
    }

    public EmployeeEvaluationDetailResponseDto confirmEvaluation(
            Long employeeEvaluationId,
            EmployeeEvaluationSaveRequestDto request
    ) {
        return updateEvaluation(employeeEvaluationId, request, true);
    }

    private EmployeeEvaluationDetailResponseDto updateEvaluation(
            Long employeeEvaluationId,
            EmployeeEvaluationSaveRequestDto request,
            boolean confirm
    ) {
        EmployeeEvaluation evaluation =
                employeeEvaluationRepository.findByIdWithDetails(employeeEvaluationId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND));

        validateEvaluationEditable(evaluation);

        List<EvaluationQuestion> questions =
                evaluationQuestionRepository
                        .findAllByIsActiveOrderBySortOrderAscEvaluationQuestionIdAsc("Y");

        if (questions.isEmpty()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }

        Map<Long, EvaluationQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(
                        EvaluationQuestion::getEvaluationQuestionId,
                        Function.identity()
                ));

        if (request.getAnswers().size() != questionMap.size()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        Set<Long> requestedQuestionIds = new HashSet<>();
        int totalScore = 0;

        for (var answerRequest : request.getAnswers()) {
            Long questionId = answerRequest.getEvaluationQuestionId();

            if (!requestedQuestionIds.add(questionId)) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST);
            }

            EvaluationQuestion question = questionMap.get(questionId);

            if (question == null) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST);
            }

            Integer score = answerRequest.getScore();

            if (score == null || score < 1 || score > question.getMaxScore()) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST);
            }

            totalScore += score;

            EmployeeEvaluationAnswer answer =
                    employeeEvaluationAnswerRepository
                            .findByEmployeeEvaluation_EmployeeEvaluationIdAndEvaluationQuestion_EvaluationQuestionId(
                                    evaluation.getEmployeeEvaluationId(),
                                    question.getEvaluationQuestionId()
                            )
                            .orElse(null);

            if (answer == null) {
                EmployeeEvaluationAnswer newAnswer =
                        EmployeeEvaluationAnswer.create(
                                evaluation,
                                question,
                                score
                        );

                employeeEvaluationAnswerRepository.save(newAnswer);
            } else {
                answer.updateScore(score);
            }
        }

        User evaluator = findLoginUserOrNull();
        String comment = normalizeNullable(request.getComment());

        if (confirm) {
            evaluation.confirm(evaluator, totalScore, comment);
        } else {
            evaluation.save(evaluator, totalScore, comment);
        }

        List<EmployeeEvaluationAnswer> savedAnswers =
                employeeEvaluationAnswerRepository
                        .findAllByEmployeeEvaluation_EmployeeEvaluationId(
                                evaluation.getEmployeeEvaluationId()
                        );

        return EmployeeEvaluationDetailResponseDto.from(
                evaluation,
                questions,
                savedAnswers
        );
    }

    private void validateDateRange(EvaluationPeriodCreateRequestDto request) {
        if (request.getTargetStartDate().isAfter(request.getTargetEndDate())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (request.getInputStartDate().isAfter(request.getInputEndDate())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
    }

    private void validateEvaluationEditable(EmployeeEvaluation evaluation) {
        if (evaluation.getEvaluationPeriod().getStatus() != EvaluationPeriodStatus.OPEN) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (evaluation.getStatus() == EmployeeEvaluationStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
    }

    private EvaluationPeriod findPeriod(Long periodId) {
        return evaluationPeriodRepository.findById(periodId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND));
    }

    private User findLoginUserOrNull() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return null;
        }

        String loginId = authentication.getName();

        if ("anonymousUser".equals(loginId)) {
            return null;
        }

        return userRepository.findByLoginId(loginId).orElse(null);
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}