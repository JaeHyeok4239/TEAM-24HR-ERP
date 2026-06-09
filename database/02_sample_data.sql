-- 0. 데이터 삭제 순서

DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM positions;
DELETE FROM departments;

-- 1. 부서 테이블 샘플 데이터

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( 1, 'CEO', '대표이사', NULL, '회사 최고 경영자' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( 2, 'MGMT', '경영지원본부', 1, '경영지원 업무 총괄' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( 3, 'FIELD', '현장관리본부', 1, '현장 운영 및 공사 관리 총괄' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( 4, 'HR', '인사팀', 2, '인사 및 채용 담당' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( 5, 'FIN', '재무회계팀', 2, '재무 및 회계 담당' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( 6, 'PUR', '구매팀', 2, '자재 구매 및 협력업체 관리' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( 7, 'DEV', '개발팀', 2, '사내 시스템 개발 및 유지보수 담당' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( 8, 'CONST', '공사관리팀', 3, '공사 일정 및 현장 관리' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( 9, 'SAFE', '안전관리팀', 3, '산업안전 및 현장 안전관리' );


-- 2. 직급 테이블 샘플 데이터

INSERT INTO positions ( position_id, position_code, position_name, description, sort_order )
VALUES ( position_seq.NEXTVAL, 'STAFF', '사원', '일반 사원', 1 );

INSERT INTO positions ( position_id, position_code, position_name, description, sort_order )
VALUES ( position_seq.NEXTVAL, 'SENIOR', '주임', '실무 담당자', 2 );

INSERT INTO positions ( position_id, position_code, position_name, description, sort_order )
VALUES ( position_seq.NEXTVAL, 'ASSISTANT_MANAGER', '대리', '실무 책임자', 3 );

INSERT INTO positions ( position_id, position_code, position_name, description, sort_order )
VALUES ( position_seq.NEXTVAL, 'MANAGER', '과장', '팀 실무 관리자', 4 );

INSERT INTO positions ( position_id, position_code, position_name, description, sort_order )
VALUES ( position_seq.NEXTVAL, 'DEPUTY_GENERAL_MANAGER', '차장', '중간 관리자', 5 );

INSERT INTO positions ( position_id, position_code, position_name, description, sort_order )
VALUES ( position_seq.NEXTVAL, 'GENERAL_MANAGER', '부장', '부서 책임자', 6 );

INSERT INTO positions ( position_id, position_code, position_name, description, sort_order )
VALUES ( position_seq.NEXTVAL, 'DIRECTOR', '이사', '임원', 7 );

INSERT INTO positions ( position_id, position_code, position_name, description, sort_order )
VALUES ( position_seq.NEXTVAL, 'CEO', '대표이사', '최고경영자', 8 );


-- 3. 역할 테이블 샘플 데이터

INSERT INTO roles ( role_id, role_code, role_name, description )
VALUES ( role_seq.NEXTVAL, 'ADMIN', '시스템 관리자', '전체 시스템 관리 권한' );

INSERT INTO roles ( role_id, role_code, role_name, description )
VALUES ( role_seq.NEXTVAL, 'USER', '일반 사용자', '기본 사용자 권한' );

INSERT INTO roles ( role_id, role_code, role_name, description )
VALUES ( role_seq.NEXTVAL, 'HR_OPERATOR', '인사 실무자', '직원 조회, 1차 등록 및 기본 인사정보 수정 권한' );

INSERT INTO roles ( role_id, role_code, role_name, description )
VALUES ( role_seq.NEXTVAL, 'HR_MANAGER', '인사 관리자', '민감정보 수정, 권한 부여 및 퇴사 처리 권한' );

INSERT INTO roles ( role_id, role_code, role_name, description )
VALUES ( role_seq.NEXTVAL, 'ATTENDANCE_MANAGER', '근태 관리자', '근태 정보 관리 권한' );

INSERT INTO roles ( role_id, role_code, role_name, description )
VALUES ( role_seq.NEXTVAL, 'PAYROLL_MANAGER', '급여 관리자', '급여 정보 관리 권한' );


-- 4. 사용자 테이블 샘플 데이터

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP001', 'ceo', '1234', '대표이사', 1, 8, 'REGULAR', CURRENT_TIMESTAMP );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP002', 'hr_manager', '1234', '인사팀장', 2, 6, 'REGULAR', CURRENT_TIMESTAMP );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP003', 'hr_operator', '1234', '인사실무자', 2, 3, 'REGULAR', CURRENT_TIMESTAMP );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP004', 'attendance', '1234', '근태담당자', 2, 3, 'REGULAR', CURRENT_TIMESTAMP );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP005', 'payroll', '1234', '급여담당자', 3, 3, 'REGULAR', CURRENT_TIMESTAMP );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP006', 'purchase_manager', '1234', '구매팀장', 4, 6, 'REGULAR', CURRENT_TIMESTAMP );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP007', 'construction_manager', '1234', '공사관리팀장', 5, 6, 'REGULAR', CURRENT_TIMESTAMP );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP008', 'safety_manager', '1234', '안전관리팀장', 6, 6, 'REGULAR', CURRENT_TIMESTAMP );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP009', 'employee01', '1234', '일반직원1', 5, 1, 'REGULAR', CURRENT_TIMESTAMP );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP010', 'employee02', '1234', '일반직원2', 4, 1, 'REGULAR', CURRENT_TIMESTAMP );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP011', 'daily01', '1234', '일용직1', 5, 1, 'DAILY', CURRENT_TIMESTAMP );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP012', 'daily02', '1234', '일용직2', 5, 1, 'DAILY', CURRENT_TIMESTAMP );


-- 5. 사용자 역할 매핑 테이블 샘플 데이터

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 1, 1 ); -- 대표이사 ADMIN

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 1, 2 ); -- 대표이사 USER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 1, 3 ); -- 대표이사 HR_OPERATOR

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 1, 4 ); -- 대표이사 HR_MANAGER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 1, 5 ); -- 대표이사 ATTENDANCE_MANAGER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 1, 6 ); -- 대표이사 PAYROLL_MANAGER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 2, 2 ); -- 인사팀장 USER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 2, 3 ); -- 인사팀장 HR_OPERATOR

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 2, 4 ); -- 인사팀장 HR_MANAGER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 3, 2 ); -- 인사실무자 USER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 3, 3 ); -- 인사실무자 HR_OPERATOR

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 4, 2 ); -- 근태담당자 USER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 4, 5 ); -- 근태담당자 ATTENDANCE_MANAGER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 5, 2 ); -- 급여담당자 USER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 5, 6 ); -- 급여담당자 PAYROLL_MANAGER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 6, 2 ); -- 구매팀장 USER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 7, 2 ); -- 공사관리팀장 USER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 8, 2 ); -- 안전관리팀장 USER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 9, 2 ); -- 일반직원1 USER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 10, 2 ); -- 일반직원2 USER


-- 근무 시간 규칙
INSERT INTO attendance_time_policies
VALUES (attendance_time_policies_seq.NEXTVAL, 'REGULAR', 'WORK', 'ALL', 900, 1800, CURRENT_TIMESTAMP, NULL);

INSERT INTO attendance_time_policies
VALUES (attendance_time_policies_seq.NEXTVAL, 'REGULAR', 'BREAK', 'ALL', 1200, 1300, CURRENT_TIMESTAMP, NULL);

-- 근태 판정 기준
INSERT INTO attendance_thresholds
VALUES (attendance_thresholds_seq.NEXTVAL, 'REGULAR', 'LATE', 10, '10분 이상 지각', CURRENT_TIMESTAMP, NULL);

INSERT INTO attendance_thresholds
VALUES (attendance_thresholds_seq.NEXTVAL, 'REGULAR', 'EARLY_LEAVE', 10, '10분 이상 조퇴', CURRENT_TIMESTAMP, NULL);

INSERT INTO attendance_thresholds
VALUES (attendance_thresholds_seq.NEXTVAL, 'REGULAR', 'ABSENCE', 240, '4시간 미만 근무 시 결근', CURRENT_TIMESTAMP, NULL);

-- 근무지
INSERT INTO workplaces
VALUES (workplaces_seq.NEXTVAL, 'HQ', '본사', '서울 본사', 100, 37.5665, 126.9780, CURRENT_TIMESTAMP, NULL);

-- 정정 종류
INSERT INTO correction_types VALUES (correction_types_seq.NEXTVAL, 'IN', '출근 정정');
INSERT INTO correction_types VALUES (correction_types_seq.NEXTVAL, 'OUT', '퇴근 정정');
INSERT INTO correction_types VALUES (correction_types_seq.NEXTVAL, 'STATUS', '근태 상태 정정');

-- 정정 사유
INSERT INTO correction_reason_types VALUES (correction_reason_types_seq.NEXTVAL, 'INPUT_ERROR', '단순 입력 오류');
INSERT INTO correction_reason_types VALUES (correction_reason_types_seq.NEXTVAL, 'LATE_DOC', '증빙 지연 제출');
INSERT INTO correction_reason_types VALUES (correction_reason_types_seq.NEXTVAL, 'OTHER', '기타');

-- 결재 상태
INSERT INTO approval_statuses VALUES (approval_statuses_seq.NEXTVAL, 'PENDING', '승인 대기');
INSERT INTO approval_statuses VALUES (approval_statuses_seq.NEXTVAL, 'APPROVED', '승인 완료');
INSERT INTO approval_statuses VALUES (approval_statuses_seq.NEXTVAL, 'REJECTED', '반려');

-- 근태 상태
INSERT INTO attendance_statuses VALUES (attendance_statuses_seq.NEXTVAL, 'WORK', '근무', 1);
INSERT INTO attendance_statuses VALUES (attendance_statuses_seq.NEXTVAL, 'LATE', '지각', 2);
INSERT INTO attendance_statuses VALUES (attendance_statuses_seq.NEXTVAL, 'EARLY_LEAVE', '조퇴', 3);
INSERT INTO attendance_statuses VALUES (attendance_statuses_seq.NEXTVAL, 'ABSENT', '결근', 4);
INSERT INTO attendance_statuses VALUES (attendance_statuses_seq.NEXTVAL, 'VACATION', '휴가', 5);
INSERT INTO attendance_statuses VALUES (attendance_statuses_seq.NEXTVAL, 'NO_CHECKOUT', '미퇴근', 6);

-- 반차 종류
INSERT INTO half_day_types VALUES (half_day_types_seq.NEXTVAL, 'ANNUAL', '연차');
INSERT INTO half_day_types VALUES (half_day_types_seq.NEXTVAL, 'HALF', '반차');

-- 전자 결재 샘플 데이터
-- 기존 사용자 테이블 참조
-- 플로우: 기안 → approval_line 결재자 결재 → document_process 처리

-- 0. 삭제 (필요 시)
DELETE FROM leave;
DELETE FROM approval_delegate;
DELETE FROM document_attach_mapping;
DELETE FROM attachment;
DELETE FROM document;
DELETE FROM approval_line;
DELETE FROM document_process;
DELETE FROM document_type;
DELETE FROM leave_type;

-- 1. 문서 유형
-- type_id 1: 연차신청서
-- type_id 2: 반차신청서
-- type_id 3: 조퇴신청서
-- type_id 4: 지출결의서
-- type_id 5: 구매요청서
INSERT INTO document_type (type_id, type_name) VALUES (document_type_seq.NEXTVAL, '연차신청서');
INSERT INTO document_type (type_id, type_name) VALUES (document_type_seq.NEXTVAL, '반차신청서');
INSERT INTO document_type (type_id, type_name) VALUES (document_type_seq.NEXTVAL, '조퇴신청서');
INSERT INTO document_type (type_id, type_name) VALUES (document_type_seq.NEXTVAL, '지출결의서');
INSERT INTO document_type (type_id, type_name) VALUES (document_type_seq.NEXTVAL, '구매요청서');


-- 2. 휴가 유형
-- type_id 1: 연차, 2: 반차, 3: 조퇴, 4: 무급휴가
INSERT INTO leave_type (type_id, type_name, is_paid) VALUES (leave_type_seq.NEXTVAL, '연차',   'Y');
INSERT INTO leave_type (type_id, type_name, is_paid) VALUES (leave_type_seq.NEXTVAL, '반차',   'Y');
INSERT INTO leave_type (type_id, type_name, is_paid) VALUES (leave_type_seq.NEXTVAL, '조퇴',   'Y');
INSERT INTO leave_type (type_id, type_name, is_paid) VALUES (leave_type_seq.NEXTVAL, '무급휴가', 'N');


-- 3. 결재선 (문서유형별 기본 결재자)
-- 연차/반차/조퇴 → 인사팀장(2)
-- 지출결의서/구매요청서 → 대표이사(1)
INSERT INTO approval_line (approval_line_id, document_type, default_approver) VALUES (approval_line_seq.NEXTVAL, 1, 2);
INSERT INTO approval_line (approval_line_id, document_type, default_approver) VALUES (approval_line_seq.NEXTVAL, 2, 2);
INSERT INTO approval_line (approval_line_id, document_type, default_approver) VALUES (approval_line_seq.NEXTVAL, 3, 2);
INSERT INTO approval_line (approval_line_id, document_type, default_approver) VALUES (approval_line_seq.NEXTVAL, 4, 1);
INSERT INTO approval_line (approval_line_id, document_type, default_approver) VALUES (approval_line_seq.NEXTVAL, 5, 1);


-- 4. 문서 처리 부서 (결재 완료 후 처리 담당)
-- 연차/반차/조퇴 → 인사팀(4), ALL
-- 지출결의서    → 재무회계팀(5), ADMIN
-- 구매요청서    → 구매팀(6), ADMIN
INSERT INTO document_process (process_id, document_type, process_department, processing_role) VALUES (document_process_seq.NEXTVAL, 1, 4, 'ALL');
INSERT INTO document_process (process_id, document_type, process_department, processing_role) VALUES (document_process_seq.NEXTVAL, 2, 4, 'ALL');
INSERT INTO document_process (process_id, document_type, process_department, processing_role) VALUES (document_process_seq.NEXTVAL, 3, 4, 'ALL');
INSERT INTO document_process (process_id, document_type, process_department, processing_role) VALUES (document_process_seq.NEXTVAL, 4, 5, 'ADMIN');
INSERT INTO document_process (process_id, document_type, process_department, processing_role) VALUES (document_process_seq.NEXTVAL, 5, 6, 'ADMIN');


-- 5. 결재 문서
-- document_id 1: 일반직원1(9) 연차 → 인사팀장(2) 결재 → 승인완료 → 인사팀 처리완료
INSERT INTO document (document_id, document_type, requester_id, approver_id, document_title, document_content, status, created_at, requested_at, approved_at)
VALUES (approval_document_seq.NEXTVAL, 1, 9, 2, '연차 신청', '개인 사정으로 연차를 신청합니다.', 'COM', SYSTIMESTAMP, SYSTIMESTAMP, SYSTIMESTAMP);

-- document_id 2: 일반직원2(10) 반차 → 인사팀장(2) 결재 → 승인완료 → 인사팀 처리 중
INSERT INTO document (document_id, document_type, requester_id, approver_id, document_title, document_content, status, created_at, requested_at, approved_at)
VALUES (approval_document_seq.NEXTVAL, 2, 10, 2, '반차 신청', '오전 반차를 신청합니다.', 'PRC', SYSTIMESTAMP, SYSTIMESTAMP, SYSTIMESTAMP);

-- document_id 3: 인사실무자(3) 조퇴 → 인사팀장(2) 결재 → 반려
INSERT INTO document (document_id, document_type, requester_id, approver_id, document_title, document_content, status, created_at, requested_at, reject_reason)
VALUES (approval_document_seq.NEXTVAL, 3, 3, 2, '조퇴 신청', '몸이 좋지 않아 조퇴를 신청합니다.', 'REJ', SYSTIMESTAMP, SYSTIMESTAMP, '당일 마감 업무로 인해 반려합니다.');

-- document_id 4: 구매팀장(6) 구매요청서 → 대표이사(1) 결재 → 결재 요청 중
INSERT INTO document (document_id, document_type, requester_id, approver_id, document_title, document_content, status, created_at, requested_at)
VALUES (approval_document_seq.NEXTVAL, 5, 6, 1, '현장 자재 구매 요청', '공사 현장 자재 구매를 요청합니다. 예상 금액: 3,500,000원', 'REQ', SYSTIMESTAMP, SYSTIMESTAMP);

-- document_id 5: 근태담당자(4) 지출결의서 → 대표이사(1) 결재 → 임시저장
INSERT INTO document (document_id, document_type, requester_id, approver_id, document_title, document_content, status, created_at)
VALUES (approval_document_seq.NEXTVAL, 4, 4, 1, '출장 교통비 지출결의', '출장 교통비 지출을 요청합니다.', 'TMP', SYSTIMESTAMP);

-- document_id 6: 공사관리팀장(7) 연차 → 인사팀장(2) 결재 → 승인 대기
INSERT INTO document (document_id, document_type, requester_id, approver_id, document_title, document_content, status, created_at, requested_at)
VALUES (approval_document_seq.NEXTVAL, 1, 7, 2, '연차 신청', '연차 휴가를 신청합니다.', 'REQ', SYSTIMESTAMP, SYSTIMESTAMP);

-- 6. 결재 위임
-- 인사팀장(2) → 인사실무자(3) 위임, 기간 만료
INSERT INTO approval_delegate (approval_delegate_id, approver_id, delegate_id, start_date, end_date, reason, is_active)
VALUES (approval_delegate_seq.NEXTVAL, 2, 3, DATE '2025-06-01', DATE '2025-06-07', '출장으로 인한 결재 위임', 'N');

-- 대표이사(1) → 인사팀장(2) 위임, 현재 활성
INSERT INTO approval_delegate (approval_delegate_id, approver_id, delegate_id, start_date, end_date, reason, is_active)
VALUES (approval_delegate_seq.NEXTVAL, 1, 2, DATE '2025-06-20', DATE '2025-06-25', '연차 휴가로 인한 결재 위임', 'Y');


-- 7. 휴가 신청 (결재 완료된 문서만)
-- 일반직원1(9) 연차 1일 (document_id=1, COM)
INSERT INTO leave (leave_id, leave_type, document_id, user_id, start_date, end_date, leave_cnt)
VALUES (leave_seq.NEXTVAL, 1, 1, 9, DATE '2025-06-10', DATE '2025-06-10', 1.00);

-- 일반직원2(10) 반차 0.5일 (document_id=2, PRC)
INSERT INTO leave (leave_id, leave_type, document_id, user_id, start_date, end_date, leave_cnt)
VALUES (leave_seq.NEXTVAL, 2, 2, 10, DATE '2025-06-11', DATE '2025-06-11', 0.50);
