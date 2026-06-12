package com.hr24.work.mail.dto;

import com.hr24.work.mail.entity.MailAttachment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MailAttachmentResponse {

    private Long attachmentId;
    private String originalName;
    private Long fileSize;
    private String fileType;

    public static MailAttachmentResponse from(MailAttachment attachment) {
        return MailAttachmentResponse.builder()
                .attachmentId(attachment.getAttachmentId())
                .originalName(attachment.getOriginalName())
                .fileSize(attachment.getFileSize())
                .fileType(attachment.getFileType())
                .build();
    }
}
