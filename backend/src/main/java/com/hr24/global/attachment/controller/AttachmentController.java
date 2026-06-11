package com.hr24.global.attachment.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hr24.global.attachment.AttachmentDownloadDto;
import com.hr24.global.attachment.AttachmentDto;
import com.hr24.global.attachment.service.AttachmentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attachment")
@Tag(name = "첨부 파일 API", description = "공용 파일 관리용 REST API")
public class AttachmentController {

    private final AttachmentService attachmentService;

    // 업로드
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<AttachmentDto> upload(
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam("uploader") Long uploader
    ) {
        return attachmentService.upload(files, uploader);
    }

    // 다운로드
    @GetMapping("/{attachmentId}/download")
    public ResponseEntity<byte[]> download(@PathVariable("attachmentId") Long attachmentId) {
        AttachmentDownloadDto file = attachmentService.download(attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getAttachmentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(file.getOriginalName(), StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .body(file.getData());
    }

    // 단건 조회
    @GetMapping("/{attachmentId}")
    public AttachmentDto getAttachment(@PathVariable("attachmentId") Long attachmentId) {
        return attachmentService.findById(attachmentId);
    }

    // 삭제
    @DeleteMapping("/{attachmentId}/delete")
    public void delete(@PathVariable("attachmentId") Long attachmentId) {
        attachmentService.delete(attachmentId);
    }
}