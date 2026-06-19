package com.hr24.employee.dto.position;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PositionUpdateRequestDto {

    @NotBlank(message = "직급명은 필수입니다.")
    private String positionName;

    @NotBlank(message = "사용 여부는 필수입니다.")
    @Pattern(
            regexp = "^[YN]$",
            message = "사용 여부는 Y 또는 N만 가능합니다."
    )
    private String isActive;
    
    @Positive(message = "바로 위 직급 ID는 양수여야 합니다.")
    private Long upperPositionId;
}