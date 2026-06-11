package com.hr24.global.attachment;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class AttachmentDto {
	
	private Long   attachmentId;
    private String originalName;
    private String storedName;
    private String attachmentType;
    private Long   attachmentSize;
    private Long   uploader;

    public static AttachmentDto from(Attachment attachment) {
        return AttachmentDto.builder()
                .attachmentId(attachment.getAttachmentId())
                .originalName(attachment.getOriginalName())
                .storedName(attachment.getStoredName())
                .attachmentType(attachment.getAttachmentType())
                .attachmentSize(attachment.getAttachmentSize())
                .uploader(attachment.getUploader().getEmployeeId())
                .build();
    }
}
