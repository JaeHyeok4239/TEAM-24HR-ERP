package com.hr24.employee.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class MyInfoUpdateRequestDto {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    private String email;

    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다.")
    private String phone;

    @Size(max = 10, message = "우편번호는 10자 이하여야 합니다.")
    private String zipcode;

    @Size(max = 255, message = "주소는 80자 이하여야 합니다.")
    private String address;

    @Size(max = 255, message = "상세주소는 80자 이하여야 합니다.")
    private String addressDetail;
}