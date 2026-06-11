package com.hr24.global.attachment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttachmentDownloadDto {

	private String originalName;
	private String attachmentType;
	private byte[] data;
}
