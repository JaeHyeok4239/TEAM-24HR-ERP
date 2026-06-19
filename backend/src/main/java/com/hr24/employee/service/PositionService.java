package com.hr24.employee.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.dto.position.PositionCreateRequestDto;
import com.hr24.employee.dto.position.PositionResponseDto;
import com.hr24.employee.dto.position.PositionUpdateRequestDto;
import com.hr24.employee.entity.Position;
import com.hr24.employee.enums.UserStatus;
import com.hr24.employee.repository.PositionRepository;
import com.hr24.employee.repository.UserRepository;
import com.hr24.global.exception.BusinessException;
import com.hr24.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PositionService {

    private static final String ACTIVE_Y = "Y";
    private static final String ACTIVE_N = "N";

    // sort_order가 NOT NULL이라서 미사용 직급은 임시로 가장 아래로 보냄
    private static final int INACTIVE_SORT_ORDER = 9999;

    private static final List<UserStatus> ASSIGNED_EMPLOYEE_STATUSES =
            List.of(
                    UserStatus.ACTIVE,
                    UserStatus.LOCKED,
                    UserStatus.LEAVE
            );

    private final PositionRepository positionRepository;
    private final UserRepository userRepository;

    // 직급 전체 조회
    public List<PositionResponseDto> findAllPositions() {
        return positionRepository
                .findAllByOrderBySortOrderAscPositionIdAsc()
                .stream()
                .map(PositionResponseDto::from)
                .toList();
    }

    // 직급 등록
    @Transactional
    public PositionResponseDto createPosition(
            PositionCreateRequestDto requestDto
    ) {
        String positionCode =
                requestDto.getPositionCode()
                        .trim()
                        .toUpperCase();

        String positionName =
                requestDto.getPositionName()
                        .trim();

        validateDuplicatePositionCode(positionCode);
        validateDuplicatePositionName(positionName);

        Position position = Position.create(
                positionCode,
                positionName,
                null,
                INACTIVE_SORT_ORDER
        );

        Position savedPosition =
                positionRepository.save(position);

        List<Position> activePositions =
                findActivePositions();

        removePositionFromList(
                activePositions,
                savedPosition.getPositionId()
        );

        insertPositionByUpperPosition(
                activePositions,
                savedPosition,
                requestDto.getUpperPositionId()
        );

        reorderActivePositions(activePositions);

        return PositionResponseDto.from(savedPosition);
    }

    // 직급 수정
    @Transactional
    public PositionResponseDto updatePosition(
            Long positionId,
            PositionUpdateRequestDto requestDto
    ) {
        Position position = findPosition(positionId);

        String positionName =
                requestDto.getPositionName()
                        .trim();

        String requestedIsActive =
                requestDto.getIsActive();

        validateDuplicatePositionName(
                positionName,
                positionId
        );

        boolean currentlyActive =
                ACTIVE_Y.equals(position.getIsActive());

        boolean requestActive =
                ACTIVE_Y.equals(requestedIsActive);

        boolean changingToInactive =
                currentlyActive && !requestActive;

        if (changingToInactive) {
            validateCanDeactivatePosition(positionId);
        }

        if (!requestActive) {
            position.update(
                    positionName,
                    null,
                    INACTIVE_SORT_ORDER,
                    ACTIVE_N
            );

            List<Position> activePositions =
                    findActivePositions();

            removePositionFromList(
                    activePositions,
                    positionId
            );

            reorderActivePositions(activePositions);

            return PositionResponseDto.from(position);
        }

        position.update(
                positionName,
                null,
                position.getSortOrder(),
                ACTIVE_Y
        );

        List<Position> activePositions =
                findActivePositions();

        removePositionFromList(
                activePositions,
                positionId
        );

        insertPositionByUpperPosition(
                activePositions,
                position,
                requestDto.getUpperPositionId()
        );

        reorderActivePositions(activePositions);

        return PositionResponseDto.from(position);
    }

    private void validateDuplicatePositionCode(
            String positionCode
    ) {
        boolean duplicateCode =
                positionRepository
                        .existsByPositionCodeIgnoreCase(positionCode);

        if (duplicateCode) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_POSITION_CODE
            );
        }
    }

    private void validateDuplicatePositionName(
            String positionName
    ) {
        boolean duplicateName =
                positionRepository
                        .existsByPositionNameIgnoreCase(positionName);

        if (duplicateName) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_POSITION_NAME
            );
        }
    }

    private void validateDuplicatePositionName(
            String positionName,
            Long positionId
    ) {
        boolean duplicateName =
                positionRepository
                        .existsByPositionNameIgnoreCaseAndPositionIdNot(
                                positionName,
                                positionId
                        );

        if (duplicateName) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_POSITION_NAME
            );
        }
    }

    private Position findPosition(Long positionId) {
        return positionRepository.findById(positionId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.POSITION_NOT_FOUND
                        )
                );
    }

    private List<Position> findActivePositions() {
        return new ArrayList<>(
                positionRepository
                        .findAllByOrderBySortOrderAscPositionIdAsc()
                        .stream()
                        .filter(position ->
                                ACTIVE_Y.equals(position.getIsActive())
                        )
                        .toList()
        );
    }

    private void removePositionFromList(
            List<Position> positions,
            Long positionId
    ) {
        positions.removeIf(position ->
                position.getPositionId().equals(positionId)
        );
    }

    private void insertPositionByUpperPosition(
            List<Position> activePositions,
            Position targetPosition,
            Long upperPositionId
    ) {
        if (upperPositionId == null) {
            activePositions.add(targetPosition);
            return;
        }

        if (
                targetPosition.getPositionId() != null
                        && targetPosition.getPositionId()
                        .equals(upperPositionId)
        ) {
            throw new BusinessException(
                    ErrorCode.INVALID_POSITION_ORDER
            );
        }

        int upperPositionIndex =
                findPositionIndex(
                        activePositions,
                        upperPositionId
                );

        if (upperPositionIndex < 0) {
            throw new BusinessException(
                    ErrorCode.INVALID_POSITION_ORDER
            );
        }

        activePositions.add(
                upperPositionIndex,
                targetPosition
        );
    }

    private int findPositionIndex(
            List<Position> positions,
            Long positionId
    ) {
        for (int i = 0; i < positions.size(); i++) {
            Position position = positions.get(i);

            if (position.getPositionId().equals(positionId)) {
                return i;
            }
        }

        return -1;
    }

    private void reorderActivePositions(
            List<Position> activePositions
    ) {
        for (int i = 0; i < activePositions.size(); i++) {
            Position position = activePositions.get(i);

            position.update(
                    position.getPositionName(),
                    position.getDescription(),
                    i + 1,
                    ACTIVE_Y
            );
        }
    }

    private void validateCanDeactivatePosition(Long positionId) {
        boolean hasAssignedEmployees =
                userRepository
                        .existsByPosition_PositionIdAndStatusIn(
                                positionId,
                                ASSIGNED_EMPLOYEE_STATUSES
                        );

        if (hasAssignedEmployees) {
            throw new BusinessException(
                    ErrorCode.POSITION_HAS_ASSIGNED_EMPLOYEES
            );
        }
    }
}