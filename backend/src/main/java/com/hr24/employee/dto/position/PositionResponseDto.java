package com.hr24.employee.dto.position;

import com.hr24.employee.entity.Position;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PositionResponseDto {

    private Long positionId;
    private String positionCode;
    private String positionName;
    private String description;
    private Integer sortOrder;
    private String isActive;

    public static PositionResponseDto from(Position position) {
        return PositionResponseDto.builder()
                .positionId(position.getPositionId())
                .positionCode(position.getPositionCode())
                .positionName(position.getPositionName())
                .description(position.getDescription())
                .sortOrder(position.getSortOrder())
                .isActive(position.getIsActive())
                .build();
    }
}