package com.hr24.work.mail.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.hr24.work.mail.entity.Mail;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MailResponse {

    private Long mailId;
    private Long senderId;
    private String senderName;
    private Long parentMailId;
    private String title;
    private String content;
    private LocalDateTime createAt;
    private int isRead;
    private List<MailAttachmentResponse> attachments;

    public static MailResponse from(Mail mail, int isRead, List<MailAttachmentResponse> attachments) {
        return MailResponse.builder()
                .mailId(mail.getMailId())
                .senderId(mail.getUser().getEmployeeId())
                .senderName(mail.getUser().getName())
                .parentMailId(mail.getParentMail() != null ? mail.getParentMail().getMailId() : null)
                .title(mail.getTitle())
                .content(mail.getContent())
                .createAt(mail.getCreateAt())
                .isRead(isRead)
                .attachments(attachments)
                .build();
    }
}
