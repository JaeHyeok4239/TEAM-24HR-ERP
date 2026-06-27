package com.hr24.auth.dto;

public record PasswordResetCodeVerifyResponseDto(
        String resetToken
) {
}