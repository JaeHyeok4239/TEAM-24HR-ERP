package com.hr24.auth.dto;

public record LoginTokenDto(
        String accessToken,
        String refreshToken
) {
}