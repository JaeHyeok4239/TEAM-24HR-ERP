package com.hr24.employee.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.employee.dto.position.PositionCreateRequestDto;
import com.hr24.employee.dto.position.PositionResponseDto;
import com.hr24.employee.dto.position.PositionUpdateRequestDto;
import com.hr24.employee.service.PositionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "직급 관리", description = "HR 기준정보 중 직급 조회, 등록, 수정 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hr/reference-data/positions")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD')")
public class PositionController {

    private final PositionService positionService;

    @Operation(
            summary = "직급 목록 조회",
            description = "등록된 전체 직급 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<PositionResponseDto>> getPositions() {
        List<PositionResponseDto> positions =
                positionService.findAllPositions();

        return ResponseEntity.ok(positions);
    }

    @Operation(
            summary = "직급 등록",
            description = "신규 직급을 등록합니다."
    )
    @PostMapping
    public ResponseEntity<PositionResponseDto> createPosition(
            @Valid @RequestBody PositionCreateRequestDto requestDto
    ) {
        PositionResponseDto responseDto =
                positionService.createPosition(requestDto);

        URI location = URI.create(
                "/api/hr/reference-data/positions/"
                        + responseDto.getPositionId()
        );

        return ResponseEntity
                .created(location)
                .body(responseDto);
    }

    @Operation(
            summary = "직급 수정",
            description = "직급명, 상위 직급, 사용 여부 등 직급 정보를 수정합니다."
    )
    @PatchMapping("/{positionId}")
    public ResponseEntity<PositionResponseDto> updatePosition(
            @PathVariable("positionId") Long positionId,
            @Valid @RequestBody PositionUpdateRequestDto requestDto
    ) {
        PositionResponseDto responseDto =
                positionService.updatePosition(
                        positionId,
                        requestDto
                );

        return ResponseEntity.ok(responseDto);
    }
}