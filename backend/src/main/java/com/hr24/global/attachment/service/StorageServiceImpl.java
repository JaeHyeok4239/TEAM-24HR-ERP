package com.hr24.global.attachment.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public String save(MultipartFile file) {
        File dir = new File(uploadPath);
        if (!dir.exists())
            dir.mkdirs();

        String original = file.getOriginalFilename();
        String storedName = UUID.randomUUID() + "_" + original;
        File target = new File(dir, storedName);

        try {
            file.transferTo(target);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + original, e);
        }

        return storedName;
    }

    @Override
    public void delete(String storedName) {
        File target = new File(uploadPath, storedName);
        if (target.exists() && !target.delete()) {
            log.warn("파일 삭제 실패: {}", storedName);
        }
    }

    @Override
    public byte[] load(String storedName) {
        File target = new File(uploadPath, storedName);
        if (!target.exists()) {
            throw new RuntimeException("파일을 찾을 수 없습니다: " + storedName);
        }
        try {
            return Files.readAllBytes(target.toPath());
        } catch (IOException e) {
            throw new RuntimeException("파일 로드 실패: " + storedName, e);
        }
    }
}