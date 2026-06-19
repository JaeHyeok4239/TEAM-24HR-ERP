package com.hr24.employee.dto.position;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PositionUpdateRequestDto {

    @NotBlank(message = "직급명은 필수입니다.")
    private String positionName;

    private String description;

    @NotNull(message = "정렬 순서는 필수입니다.")
    private Integer sortOrder;

    @NotBlank(message = "사용 여부는 필수입니다.")
    @Pattern(
            regexp = "^[YN]$",
            message = "사용 여부는 Y 또는 N만 가능합니다."
    )
    private String isActive;
}