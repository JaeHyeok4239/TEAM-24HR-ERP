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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hr/reference-data/positions")
@PreAuthorize("hasAnyRole('ADMIN', 'HR_LEAD')")
public class PositionController {

    private final PositionService positionService;

    // 직급 조회
    @GetMapping
    public ResponseEntity<List<PositionResponseDto>> getPositions() {
        List<PositionResponseDto> positions =
                positionService.findAllPositions();

        return ResponseEntity.ok(positions);
    }

    // 직급 등록
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

    // 직급 수정
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