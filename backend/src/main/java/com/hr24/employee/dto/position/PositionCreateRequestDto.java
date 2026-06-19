package com.hr24.employee.dto.position;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PositionCreateRequestDto {

    @NotBlank(message = "직급 코드는 필수입니다.")
    @Pattern(
            regexp = "^[A-Z0-9_-]+$",
            message = "직급 코드는 대문자, 숫자, _, -만 사용할 수 있습니다."
    )
    private String positionCode;

    @NotBlank(message = "직급명은 필수입니다.")
    private String positionName;

    @Positive(message = "바로 위 직급 ID는 양수여야 합니다.")
    private Long upperPositionId;
}