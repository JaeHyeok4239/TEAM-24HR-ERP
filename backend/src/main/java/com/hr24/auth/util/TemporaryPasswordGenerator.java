package com.hr24.auth.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class TemporaryPasswordGenerator {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL = "!@#$%";
    private static final String ALL = UPPER + LOWER + NUMBER + SPECIAL;

    private static final int LENGTH = 12;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        StringBuilder password = new StringBuilder();

        password.append(randomChar(UPPER));
        password.append(randomChar(LOWER));
        password.append(randomChar(NUMBER));
        password.append(randomChar(SPECIAL));

        while (password.length() < LENGTH) {
            password.append(randomChar(ALL));
        }

        return shuffle(password.toString());
    }

    private char randomChar(String source) {
        return source.charAt(secureRandom.nextInt(source.length()));
    }

    private String shuffle(String value) {
        char[] chars = value.toCharArray();

        for (int i = chars.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);

            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }
}