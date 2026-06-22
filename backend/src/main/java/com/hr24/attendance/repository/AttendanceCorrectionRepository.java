package com.hr24.attendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hr24.attendance.entity.AttendanceCorrection;
import com.hr24.attendance.entity.AttendanceResult;
import com.hr24.document.entity.Document;

public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrection, Long>{
	// 특정 근태 결과에 대한 정정 목록 조회
    List<AttendanceCorrection> findByCorrectionTarget(AttendanceResult result);

    // 문서로 정정 이력 조회
    Optional<AttendanceCorrection> findByDocument(Document document);

    // 문서 ID로 조회
    Optional<AttendanceCorrection> findByDocumentDocumentId(Long documentId);
	
}
