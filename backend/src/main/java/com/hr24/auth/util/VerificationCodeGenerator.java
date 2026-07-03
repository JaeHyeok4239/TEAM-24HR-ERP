package com.hr24.auth.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class VerificationCodeGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        int code = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}