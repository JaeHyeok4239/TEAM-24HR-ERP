package com.hr24.document.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

//업무 처리 관련 서비스 - 예시 코드
public class DocumentProcessService {
	
//    private final DocumentRepository documentRepository;
//    private final UserRepository userRepository;
//    private final HrService hrService;
//    private final AttendanceCorrectionService attendanceCorrectionService;

    // 업무처리함 조회 (내가 처리해야 할 문서 목록)
//    public List<DocumentResponseDto.ProcessBoxDto> myProcessBox(String loginId, Pageable pageable) {
//        User user = userRepository.findByLoginId(loginId)
//                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));
//
//        return documentRepository.findMyProcessBox(user.getEmployeeId(), pageable)
//                .stream()
//                .map(DocumentResponseDto.ProcessBoxDto::from)
//                .toList();
//    }

    // 최종 승인 후 실제 업무 반영 (ApprovalService가 마지막 단계 승인 처리 후 호출)
//    @Transactional
//    public void processApprovedDocument(Long documentId) {
//        Document document = documentRepository.findById(documentId)
//                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서입니다"));
//
//        String detailTable = document.getDocumentType().getDetailTable();
//
//        if ("leave".equals(detailTable)) {
//            hrService.processLeave(document);
//        } else if ("attendance_correction".equals(detailTable)) {
//            attendanceCorrectionService.process(document);
//        }
//        // else if ("purchase".equals(detailTable)) { ... }
//
//        document.setStatus("APR");
//        document.setProcessedAt(LocalDateTime.now());
//    }

    // 반려 처리 (반려 시 detail 테이블 후처리가 필요하면 여기서)
//    @Transactional
//    public void processRejectedDocument(Long documentId, String rejectReason) {
//        Document document = documentRepository.findById(documentId)
//                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 문서입니다"));
//
//        document.setStatus("REJ");
//        document.setRejectReason(rejectReason);
//        document.setProcessedAt(LocalDateTime.now());
//    }

    // 문서 상세 처리 화면용 조회 (결재이력 + detail 데이터 포함)
//    public DocumentResponseDto.DocumentDto viewForProcess(Long documentId) {
//        // viewDocument와 유사하지만 처리자 권한 체크 포함
//        ...
//    }
}
