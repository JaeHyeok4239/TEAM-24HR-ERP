package com.hr24.global.attachment.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hr24.employee.entity.User;
import com.hr24.employee.repository.UserRepository;
import com.hr24.global.attachment.Attachment;
import com.hr24.global.attachment.AttachmentDownloadDto;
import com.hr24.global.attachment.AttachmentDto;
import com.hr24.global.attachment.AttachmentRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {

	private final AttachmentRepository attachmentRepository;
	private final UserRepository userRepository;
	private final StorageService storageService; // 주입

	public List<Attachment> upload(@NonNull List<MultipartFile> files, @NonNull Long uploader) {
        User uploaderId = userRepository.findById(uploader)
                .orElseThrow(() -> new EntityNotFoundException("업로더를 찾을 수 없습니다."));

        return files.stream().map(file -> {
            
        	String storedName = storageService.save(file);
			

            Attachment attachment = Attachment.builder()
                    .originalName(file.getOriginalFilename())
                    .storedName(storedName)
                    .attachmentType(file.getContentType())
                    .attachmentSize(file.getSize())
                    .uploader(uploaderId)
                    .uploadTime(LocalDateTime.now())
                    .build();

            return attachmentRepository.save(attachment);
            
        	}).toList();
    
	}

	@Transactional
	public void delete(@NonNull Long attachmentId) {
		Attachment attachment = attachmentRepository.findById(attachmentId)
				.orElseThrow(() -> new EntityNotFoundException("첨부파일을 찾을 수 없습니다."));

		storageService.delete(attachment.getStoredName()); // 삭제 위임
		attachmentRepository.delete(attachment);
	}
	

	public AttachmentDto findById(@NonNull Long attachmentId) {
		Attachment attachment = attachmentRepository.findById(attachmentId)
				.orElseThrow(() -> new EntityNotFoundException("첨부파일을 찾을 수 없습니다."));

		return AttachmentDto.from(attachment);
	}
	
	//여러 파일 삭제
	@Transactional
	public void deleteAll(List<Long> attachmentIds) {
	    List<Attachment> attachments = attachmentRepository.findAllById(attachmentIds); // SELECT 1번

	    attachments.forEach(attachment -> storageService.delete(attachment.getStoredName())); // 파일 삭제는 IO라 N번 불가피

	    attachmentRepository.deleteAllByIdInBatch(attachmentIds); // DB row는 단일 DELETE
	}

	public AttachmentDownloadDto download(@NonNull Long attachmentId) {
		Attachment attachment = attachmentRepository.findById(attachmentId)
				.orElseThrow(() -> new EntityNotFoundException("첨부파일을 찾을 수 없습니다."));

		byte[] data = storageService.load(attachment.getStoredName());

		return new AttachmentDownloadDto(attachment.getOriginalName(), attachment.getAttachmentType(), data);
	}

}