package com.hr24.work.mail.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.work.mail.dto.MailAttachmentResponse;
import com.hr24.work.mail.dto.MailResponse;
import com.hr24.work.mail.dto.MailSendRequest;
import com.hr24.work.mail.entity.Mail;
import com.hr24.work.mail.entity.MailReceiver;
import com.hr24.work.mail.repository.MailAttachmentRepository;
import com.hr24.work.mail.repository.MailReceiverRepository;
import com.hr24.work.mail.repository.MailRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final MailRepository mailRepository;
    private final MailReceiverRepository mailReceiverRepository;
    private final MailAttachmentRepository mailAttachmentRepository;
    private final UserRepository userRepository;

    // 메일 발송 - 수신자별 mail_receiver 레코드 생성
    @Transactional
    public void sendMail(Long senderId, MailSendRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        // 답장이면 원본 메일 연결, 최초 메일이면 null
        Mail parentMail = null;
        if (request.getParentMailId() != null) {
            parentMail = mailRepository.findById(request.getParentMailId())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 메일입니다."));
        }

        // 메일 본문 저장
        Mail mail = Mail.builder()
                .user(sender)
                .parentMail(parentMail)
                .title(request.getTitle())
                .content(request.getContent())
                .createAt(LocalDateTime.now())
                .build();

        mailRepository.save(mail);

        // 수신자마다 mail_receiver 레코드 생성 (초기 읽음여부: 0, 삭제여부: 0)
        for (Long receiverId : request.getReceiverIds()) {
            User receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 수신자입니다."));

            mailReceiverRepository.save(MailReceiver.builder()
                    .mail(mail)
                    .user(receiver)
                    .isRead(0)
                    .isDeleted(0)
                    .build());
        }
    }

    // 받은 메일함 조회 - 삭제하지 않은 메일만 반환
    public List<MailResponse> getInbox(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        return mailReceiverRepository.findByUserAndIsDeleted(user, 0).stream()
                .map(receiver -> {
                    List<MailAttachmentResponse> attachments = getAttachments(receiver.getMail());
                    return MailResponse.from(receiver.getMail(), receiver.getIsRead(), attachments);
                })
                .collect(Collectors.toList());
    }

    // 보낸 메일함 조회
    public List<MailResponse> getSentMails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        return mailRepository.findByUser(user).stream()
                .map(mail -> MailResponse.from(mail, 1, getAttachments(mail)))
                .collect(Collectors.toList());
    }

    // 메일 읽기 - 읽음 여부를 1로 업데이트 후 상세 내용 반환
    @Transactional
    public MailResponse readMail(Long userId, Long mailId) {
        // 사용자 존재 여부만 검증 (변수 미사용 경고 방지)
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        Mail mail = mailRepository.findById(mailId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 메일입니다."));

        // 수신자 레코드에서 읽음 처리
        mailReceiverRepository.findByMail(mail).stream()
                .filter(r -> r.getUser().getEmployeeId().equals(userId))
                .findFirst()
                .ifPresent(r -> {
                    r.setIsRead(1);
                    mailReceiverRepository.save(r);
                });

        return MailResponse.from(mail, 1, getAttachments(mail));
    }

    // 메일 삭제 - 실제 삭제 대신 is_deleted를 1로 변경 (소프트 삭제)
    @Transactional
    public void deleteMail(Long userId, Long mailId) {
        Mail mail = mailRepository.findById(mailId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 메일입니다."));

        mailReceiverRepository.findByMail(mail).stream()
                .filter(r -> r.getUser().getEmployeeId().equals(userId))
                .findFirst()
                .ifPresent(r -> {
                    r.setIsDeleted(1);
                    mailReceiverRepository.save(r);
                });
    }

    // 메일의 첨부파일 목록을 MailAttachmentResponse로 변환하는 공통 메서드
    private List<MailAttachmentResponse> getAttachments(Mail mail) {
        return mailAttachmentRepository.findByMail(mail).stream()
                .map(MailAttachmentResponse::from)
                .collect(Collectors.toList());
    }
}
