package com.hr24.global.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountMailService {

    private final ExternalMailService externalMailService;

    public void sendAccountCreatedMail(
            String to,
            String name,
            String loginId,
            String temporaryPassword
    ) {
        String subject = "[24HR ERP] 계정이 생성되었습니다.";

        String content = """
                안녕하세요. %s님.

                24HR ERP 계정이 생성되었습니다.

                로그인 ID: %s
                임시 비밀번호: %s

                최초 로그인 후 반드시 비밀번호를 변경해야 시스템을 이용할 수 있습니다.

                감사합니다.
                24HR ERP
                """.formatted(name, loginId, temporaryPassword);

        externalMailService.send(to, subject, content);
    }

    public void sendPasswordResetCodeMail(
            String to,
            String name,
            String code
    ) {
        String subject = "[24HR ERP] 비밀번호 재설정 인증코드";

        String content = """
                안녕하세요. %s님.

                비밀번호 재설정을 위한 인증코드입니다.

                인증코드: %s

                인증코드는 5분 동안만 유효합니다.
                본인이 요청하지 않았다면 이 메일을 무시해주세요.

                감사합니다.
                24HR ERP
                """.formatted(name, code);

        externalMailService.send(to, subject, content);
    }
}