package com.hr24.evaluation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.evaluation.dto.EmployeeEvaluationDetailResponseDto;
import com.hr24.evaluation.dto.EmployeeEvaluationHistoryResponseDto;
import com.hr24.evaluation.dto.EmployeeEvaluationSaveRequestDto;
import com.hr24.evaluation.dto.EmployeeEvaluationTargetResponseDto;
import com.hr24.evaluation.dto.EvaluationPeriodCreateRequestDto;
import com.hr24.evaluation.dto.EvaluationPeriodResponseDto;
import com.hr24.evaluation.dto.EvaluationResultSummaryResponseDto;
import com.hr24.evaluation.dto.PromotionCandidateResponseDto;
import com.hr24.evaluation.service.EvaluationCommandService;
import com.hr24.evaluation.service.EvaluationQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hr/evaluations")
public class EvaluationController {

    private final EvaluationCommandService evaluationCommandService;
    private final EvaluationQueryService evaluationQueryService;

    @GetMapping("/periods")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<List<EvaluationPeriodResponseDto>> findPeriods() {
        List<EvaluationPeriodResponseDto> periods =
                evaluationQueryService.findPeriods();

        return ResponseEntity.ok(periods);
    }

    @PostMapping("/periods")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<EvaluationPeriodResponseDto> createPeriod(
            @Valid @RequestBody EvaluationPeriodCreateRequestDto request
    ) {
        EvaluationPeriodResponseDto period =
                evaluationCommandService.createPeriod(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(period);
    }

    @PatchMapping("/periods/{periodId}/open")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<EvaluationPeriodResponseDto> openPeriod(
            @PathVariable("periodId") Long periodId
    ) {
        EvaluationPeriodResponseDto period =
                evaluationCommandService.openPeriod(periodId);

        return ResponseEntity.ok(period);
    }

    @PatchMapping("/periods/{periodId}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<EvaluationPeriodResponseDto> closePeriod(
            @PathVariable("periodId") Long periodId
    ) {
        EvaluationPeriodResponseDto period =
                evaluationCommandService.closePeriod(periodId);

        return ResponseEntity.ok(period);
    }

    @PostMapping("/periods/{periodId}/targets")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<List<EmployeeEvaluationTargetResponseDto>> createTargets(
            @PathVariable("periodId") Long periodId
    ) {
        List<EmployeeEvaluationTargetResponseDto> targets =
                evaluationCommandService.createTargets(periodId);

        return ResponseEntity.status(HttpStatus.CREATED).body(targets);
    }

    @GetMapping("/periods/{periodId}/targets")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<List<EmployeeEvaluationTargetResponseDto>> findTargets(
            @PathVariable("periodId") Long periodId
    ) {
        List<EmployeeEvaluationTargetResponseDto> targets =
                evaluationQueryService.findTargets(periodId);

        return ResponseEntity.ok(targets);
    }

    @GetMapping("/{employeeEvaluationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<EmployeeEvaluationDetailResponseDto> findEvaluationDetail(
            @PathVariable("employeeEvaluationId") Long employeeEvaluationId
    ) {
        EmployeeEvaluationDetailResponseDto evaluation =
                evaluationQueryService.findEvaluationDetail(employeeEvaluationId);

        return ResponseEntity.ok(evaluation);
    }

    @PatchMapping("/{employeeEvaluationId}/save")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<EmployeeEvaluationDetailResponseDto> saveEvaluation(
            @PathVariable("employeeEvaluationId") Long employeeEvaluationId,
            @Valid @RequestBody EmployeeEvaluationSaveRequestDto request
    ) {
        EmployeeEvaluationDetailResponseDto evaluation =
                evaluationCommandService.saveEvaluation(
                        employeeEvaluationId,
                        request
                );

        return ResponseEntity.ok(evaluation);
    }

    @PatchMapping("/{employeeEvaluationId}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<EmployeeEvaluationDetailResponseDto> confirmEvaluation(
            @PathVariable("employeeEvaluationId") Long employeeEvaluationId,
            @Valid @RequestBody EmployeeEvaluationSaveRequestDto request
    ) {
        EmployeeEvaluationDetailResponseDto evaluation =
                evaluationCommandService.confirmEvaluation(
                        employeeEvaluationId,
                        request
                );

        return ResponseEntity.ok(evaluation);
    }

    @GetMapping("/results")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<List<EvaluationResultSummaryResponseDto>> findResults() {
        List<EvaluationResultSummaryResponseDto> results =
                evaluationQueryService.findResultSummaries();

        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<List<EmployeeEvaluationHistoryResponseDto>> findEmployeeHistories(
            @PathVariable("employeeId") Long employeeId
    ) {
        List<EmployeeEvaluationHistoryResponseDto> histories =
                evaluationQueryService.findEmployeeHistories(employeeId);

        return ResponseEntity.ok(histories);
    }

    @GetMapping("/promotion-candidates")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'HR_OPERATOR')")
    public ResponseEntity<List<PromotionCandidateResponseDto>> findPromotionCandidates() {
        List<PromotionCandidateResponseDto> candidates =
                evaluationQueryService.findPromotionCandidates();

        return ResponseEntity.ok(candidates);
    }
}