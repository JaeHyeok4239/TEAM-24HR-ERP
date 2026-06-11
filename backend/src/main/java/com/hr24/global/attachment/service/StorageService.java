package com.hr24.global.attachment.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

//파일 저장 인터페이스 - 추후 파일 서버 확장 위해 인터페이스 분리
public interface StorageService {
	
	String save(MultipartFile file) throws IOException;
	
	void delete(String storedName);
	
	byte[] load(String storedName) throws IOException;
}
