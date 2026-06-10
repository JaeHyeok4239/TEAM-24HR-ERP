package com.hr24.global.attachment.service;

import java.io.File;
import java.io.FileNotFoundException;
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
	
	//저장 경로
	@Value("${file.upload-path}")
	private String uploadPath;
	
	//파일 저장
	@Override
	public String save(MultipartFile file) throws IOException {
		
		//파일 저장 폴더 생성
		File dir = new File(uploadPath);
		if (!dir.exists())
			dir.mkdirs();

		String original = file.getOriginalFilename();
		String storedName = UUID.randomUUID() + "_" + original;

		File target = new File(dir, storedName);
		file.transferTo(target);

		return storedName;
	}
	
	//파일 삭제
	@Override
	public void delete(String storedName) {
	    File target = new File(uploadPath, storedName);
	    if (target.exists() && !target.delete()) {
	        log.warn("파일 삭제 실패: {}", storedName);
	    }
	}
	
	//파일 불러오기
	@Override
	public byte[] load(String storedName) throws IOException {
	    File target = new File(uploadPath, storedName);
	    if (!target.exists()) {
	        throw new FileNotFoundException("파일을 찾을 수 없습니다: " + storedName);
	    }
	    return Files.readAllBytes(target.toPath());
	}
}
