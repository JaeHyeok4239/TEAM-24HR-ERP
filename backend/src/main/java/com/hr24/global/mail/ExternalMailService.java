package com.hr24.global.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalMailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${app.mail.from:no-reply@24hr.local}")
    private String from;

    public void send(String to, String subject, String content) {

        if (!mailEnabled) {
            log.info("[MAIL SKIP] 메일 발송 기능이 비활성화되어 있습니다. to={}, subject={}", to, subject);
            return;
        }

        if (!StringUtils.hasText(mailUsername) || !StringUtils.hasText(mailPassword)) {
            log.warn("[MAIL SKIP] 메일 계정 설정이 없습니다. MAIL_USERNAME 또는 MAIL_PASSWORD를 확인하세요.");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        javaMailSender.send(message);

        log.info("[MAIL SEND] 메일 발송 완료. to={}, subject={}", to, subject);
    }
}