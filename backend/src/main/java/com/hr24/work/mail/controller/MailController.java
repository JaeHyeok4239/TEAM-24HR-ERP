package com.hr24.work.mail.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr24.work.mail.dto.MailResponse;
import com.hr24.work.mail.dto.MailSendRequest;
import com.hr24.work.mail.service.MailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "사내 메일", description = "사내 메일 발송 및 조회 API")
@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    // 메일 발송 - 수신자 여러 명 지정 가능, 답장 시 parentMailId 포함
    @Operation(summary = "메일 발송", description = "사내 메일을 발송합니다. parentMailId를 포함하면 답장으로 처리됩니다.")
    @PostMapping("/send")
    public ResponseEntity<Void> sendMail(
            @RequestParam Long senderId,
            @RequestBody MailSendRequest request) {
        mailService.sendMail(senderId, request);
        return ResponseEntity.noContent().build();
    }

    // 받은 메일함 - 삭제 처리되지 않은 메일만 반환
    @Operation(summary = "받은 메일함 조회", description = "사용자의 받은 메일 목록(삭제 제외)을 반환합니다.")
    @GetMapping("/inbox")
    public ResponseEntity<List<MailResponse>> getInbox(@RequestParam Long userId) {
        return ResponseEntity.ok(mailService.getInbox(userId));
    }

    // 보낸 메일함 - 해당 사용자가 발신한 메일 목록 반환
    @Operation(summary = "보낸 메일함 조회", description = "사용자가 발송한 메일 목록을 반환합니다.")
    @GetMapping("/sent")
    public ResponseEntity<List<MailResponse>> getSentMails(@RequestParam Long userId) {
        return ResponseEntity.ok(mailService.getSentMails(userId));
    }

    // 메일 읽기 - 읽음 상태로 변경 후 메일 상세 반환
    @Operation(summary = "메일 읽기", description = "메일을 읽음 처리하고 상세 내용을 반환합니다.")
    @GetMapping("/{mailId}")
    public ResponseEntity<MailResponse> readMail(
            @RequestParam Long userId,
            @PathVariable Long mailId) {
        return ResponseEntity.ok(mailService.readMail(userId, mailId));
    }

    // 메일 삭제 - 실제 삭제가 아닌 소프트 삭제 (is_deleted = 1)
    @Operation(summary = "메일 삭제", description = "메일을 소프트 삭제 처리합니다. (is_deleted = 1)")
    @DeleteMapping("/{mailId}")
    public ResponseEntity<Void> deleteMail(
            @RequestParam Long userId,
            @PathVariable Long mailId) {
        mailService.deleteMail(userId, mailId);
        return ResponseEntity.noContent().build();
    }
}
