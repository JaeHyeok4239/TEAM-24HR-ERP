package com.hr24.global.attachment.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.global.attachment.Attachment;
import com.hr24.global.attachment.AttachmentDto;
import com.hr24.global.attachment.AttachmentRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {

	private final AttachmentRepository attachmentRepository;
	private final UserRepository userRepository;
	private final StorageService storageService;

	public List<AttachmentDto> upload(List<MultipartFile> files, Long uploader) {
		User uploaderId = userRepository.findById(uploader)
				.orElseThrow(() -> new EntityNotFoundException("업로더를 찾을 수 없습니다."));

		return files.stream().map(file -> {
			
			String storedName;
			
			try {
				storedName = storageService.save(file); //저장 위임
			} catch (IOException e) {
				
				throw new RuntimeException("파일 저장 실패 : " + file.getOriginalFilename());
			}

			Attachment attachment = Attachment.builder().originalName(file.getOriginalFilename()).storedName(storedName)
					.attachmentType(file.getContentType()).attachmentSize(file.getSize()).uploader(uploaderId)
					.uploadTime(LocalDateTime.now()).build();

			return AttachmentDto.from(attachmentRepository.save(attachment));
		}).collect(Collectors.toList());
	}

	@Transactional
	public void delete(Long attachmentId) {
		Attachment attachment = attachmentRepository.findById(attachmentId)
				.orElseThrow(() -> new EntityNotFoundException("첨부파일을 찾을 수 없습니다."));

		storageService.delete(attachment.getStoredName()); // 삭제 위임
		attachmentRepository.delete(attachment);
	}
}
