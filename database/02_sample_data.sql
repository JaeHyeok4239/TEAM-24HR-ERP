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





-- 테이블 삭제
DELETE FROM attendance_logs;
DELETE FROM attendance_results;
DELETE FROM attendance_time_policies;
DELETE FROM attendance_thresholds;
DELETE FROM workplaces;

-- 코드 테이블 삭제
DELETE FROM correction_types;
DELETE FROM correction_reason_types;
DELETE FROM approval_statuses;
DELETE FROM attendance_statuses;
DELETE FROM half_day_types;

-- 근무 시간 규칙
INSERT INTO attendance_time_policies
VALUES (attendance_time_policies_seq.NEXTVAL, 'REGULAR', 'WORK', 'MON', 900, 1800, CURRENT_TIMESTAMP, NULL);

INSERT INTO attendance_time_policies
VALUES (attendance_time_policies_seq.NEXTVAL, 'REGULAR', 'BREAK', 'THU', 1200, 1300, CURRENT_TIMESTAMP, NULL);

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
INSERT INTO half_day_types VALUES (half_day_types_seq.NEXTVAL, 'AM', '오전 반차');
INSERT INTO half_day_types VALUES (half_day_types_seq.NEXTVAL, 'PM', '오후 반차');





-- 전자결재 샘플 데이터
-- 기존 사용자 테이블 참조
-- 플로우: 기안(TMP/REQ) → 결재(APR/REJ) → 처리(PRC→COM)
-- 결재 방식 : 다단계 승인(approval_history 에서 처리)

-- 0. 삭제
DELETE FROM leave;
DELETE FROM approval_delegate;
DELETE FROM approval_history;
DELETE FROM document_attach_mapping;
DELETE FROM attachment;
DELETE FROM document;
DELETE FROM approval_line;
DELETE FROM document_process;
DELETE FROM document_type;
DELETE FROM leave_type;


-- 1. 문서 유형
INSERT INTO document_type (type_id, type_name, detail_table) VALUES (document_type_seq.NEXTVAL, '연차신청서', 'leave');
INSERT INTO document_type (type_id, type_name, detail_table) VALUES (document_type_seq.NEXTVAL, '반차신청서', 'leave');
INSERT INTO document_type (type_id, type_name, detail_table) VALUES (document_type_seq.NEXTVAL, '조퇴신청서', 'leave');
INSERT INTO document_type (type_id, type_name, detail_table) VALUES (document_type_seq.NEXTVAL, '지출결의서', NULL);
INSERT INTO document_type (type_id, type_name, detail_table) VALUES (document_type_seq.NEXTVAL, '구매요청서', NULL);


-- 2. 휴가 유형
INSERT INTO leave_type (type_id, type_name, is_paid) VALUES (leave_type_seq.NEXTVAL, '연차',    'Y');
INSERT INTO leave_type (type_id, type_name, is_paid) VALUES (leave_type_seq.NEXTVAL, '반차',    'Y');
INSERT INTO leave_type (type_id, type_name, is_paid) VALUES (leave_type_seq.NEXTVAL, '조퇴',    'Y');
INSERT INTO leave_type (type_id, type_name, is_paid) VALUES (leave_type_seq.NEXTVAL, '무급휴가', 'N');


-- 3. 결재선 (step_order 포함)
-- 연차/반차/조퇴: 1단계 인사팀장(2)
-- 지출결의서:    1단계 인사팀장(2) → 2단계 대표이사(1)
-- 구매요청서:    1단계 구매팀장(6) → 2단계 대표이사(1)
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver) VALUES (approval_line_seq.NEXTVAL, 1, 1, 2);
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver) VALUES (approval_line_seq.NEXTVAL, 2, 1, 2);
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver) VALUES (approval_line_seq.NEXTVAL, 3, 1, 2);
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver) VALUES (approval_line_seq.NEXTVAL, 4, 1, 2); -- 지출결의 1차: 인사팀장
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver) VALUES (approval_line_seq.NEXTVAL, 4, 2, 1); -- 지출결의 2차: 대표이사
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver) VALUES (approval_line_seq.NEXTVAL, 5, 1, 6); -- 구매요청 1차: 구매팀장
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver) VALUES (approval_line_seq.NEXTVAL, 5, 2, 1); -- 구매요청 2차: 대표이사


-- 4. 문서 처리 부서
INSERT INTO document_process (process_id, document_type, process_department, processing_role) VALUES (document_process_seq.NEXTVAL, 1, 4, 'ALL');
INSERT INTO document_process (process_id, document_type, process_department, processing_role) VALUES (document_process_seq.NEXTVAL, 2, 4, 'ALL');
INSERT INTO document_process (process_id, document_type, process_department, processing_role) VALUES (document_process_seq.NEXTVAL, 3, 4, 'ALL');
INSERT INTO document_process (process_id, document_type, process_department, processing_role) VALUES (document_process_seq.NEXTVAL, 4, 5, 'ADMIN');
INSERT INTO document_process (process_id, document_type, process_department, processing_role) VALUES (document_process_seq.NEXTVAL, 5, 6, 'ADMIN');


-- 5. 결재 문서
-- [COM] 일반직원1(9) 연차 → 1단계 승인 → 인사실무자(3) 처리완료
INSERT INTO document (document_id, document_type, requester_id, processor_id, document_title, status, created_at, requested_at, processed_at)
VALUES (approval_document_seq.NEXTVAL, 1, 9, 3, '연차 신청', 'COM', SYSTIMESTAMP, SYSTIMESTAMP, SYSTIMESTAMP);

-- [PRC] 일반직원2(10) 반차 → 1단계 승인 → 인사실무자(3) 처리 중
INSERT INTO document (document_id, document_type, requester_id, processor_id, document_title, status, created_at, requested_at, processed_at)
VALUES (approval_document_seq.NEXTVAL, 2, 10, 3, '반차 신청', 'PRC', SYSTIMESTAMP, SYSTIMESTAMP, SYSTIMESTAMP);

-- [REJ] 인사실무자(3) 조퇴 → 1단계 반려
INSERT INTO document (document_id, document_type, requester_id, processor_id, document_title, status, created_at, requested_at, reject_reason)
VALUES (approval_document_seq.NEXTVAL, 3, 3, NULL, '조퇴 신청', 'REJ', SYSTIMESTAMP, SYSTIMESTAMP, '당일 마감 업무로 인해 반려합니다.');

-- [REQ] 구매팀장(6) 구매요청서 → 1단계 결재 대기
INSERT INTO document (document_id, document_type, requester_id, processor_id, document_title, status, created_at, requested_at)
VALUES (approval_document_seq.NEXTVAL, 5, 6, NULL, '현장 자재 구매 요청', 'REQ', SYSTIMESTAMP, SYSTIMESTAMP);

-- [TMP] 근태담당자(4) 지출결의서 임시저장
INSERT INTO document (document_id, document_type, requester_id, processor_id, document_title, status, created_at)
VALUES (approval_document_seq.NEXTVAL, 4, 4, NULL, '출장 교통비 지출결의', 'TMP', SYSTIMESTAMP);

-- [REQ] 공사관리팀장(7) 연차 → 1단계 결재 대기
INSERT INTO document (document_id, document_type, requester_id, processor_id, document_title, status, created_at, requested_at)
VALUES (approval_document_seq.NEXTVAL, 1, 7, NULL, '연차 신청', 'REQ', SYSTIMESTAMP, SYSTIMESTAMP);

-- [APR] 근태담당자(4) 구매요청서 → 1단계 승인 → 2단계 대기
INSERT INTO document (document_id, document_type, requester_id, processor_id, document_title, status, created_at, requested_at)
VALUES (approval_document_seq.NEXTVAL, 5, 4, NULL, '사무용품 구매 요청', 'REQ', SYSTIMESTAMP, SYSTIMESTAMP);


-- 6. 결재 이력 (approval_history)
-- document_id=1 (연차, COM) → 1단계 승인완료
INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status, approver_comment, acted_at)
VALUES (approval_history_seq.NEXTVAL, 1, 1, 2, 'APR', '승인합니다.', SYSTIMESTAMP);

-- document_id=2 (반차, PRC) → 1단계 승인완료
INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status, approver_comment, acted_at)
VALUES (approval_history_seq.NEXTVAL, 2, 1, 2, 'APR', '승인합니다.', SYSTIMESTAMP);

-- document_id=3 (조퇴, REJ) → 1단계 반려
INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status, approver_comment, acted_at)
VALUES (approval_history_seq.NEXTVAL, 3, 1, 2, 'REJ', '당일 마감 업무로 인해 반려합니다.', SYSTIMESTAMP);

-- document_id=4 (구매요청, REQ) → 1단계 대기
INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status, approver_comment, acted_at)
VALUES (approval_history_seq.NEXTVAL, 4, 1, 6, 'PND', NULL, NULL);

-- document_id=6 (연차, REQ) → 1단계 대기
INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status, approver_comment, acted_at)
VALUES (approval_history_seq.NEXTVAL, 6, 1, 2, 'PND', NULL, NULL);

-- document_id=7 (구매요청 2단계) → 1단계 승인 → 2단계 대기
INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status, approver_comment, acted_at)
VALUES (approval_history_seq.NEXTVAL, 7, 1, 6, 'APR', '구매 필요성 확인. 승인합니다.', SYSTIMESTAMP);

INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status, approver_comment, acted_at)
VALUES (approval_history_seq.NEXTVAL, 7, 2, 1, 'PND', NULL, NULL);

-- 7. 결재 위임
INSERT INTO approval_delegate (approval_delegate_id, approver_id, delegate_id, start_date, end_date, reason, is_active)
VALUES (approval_delegate_seq.NEXTVAL, 2, 3, DATE '2025-06-01', DATE '2025-06-07', '출장으로 인한 결재 위임', 'N');

INSERT INTO approval_delegate (approval_delegate_id, approver_id, delegate_id, start_date, end_date, reason, is_active)
VALUES (approval_delegate_seq.NEXTVAL, 1, 2, DATE '2025-06-20', DATE '2025-06-25', '연차 휴가로 인한 결재 위임', 'Y');


-- 8. 휴가 데이터
INSERT INTO leave (leave_id, leave_type, document_id, start_date, end_date, leave_cnt)
VALUES (leave_seq.NEXTVAL, 1, 1, DATE '2025-06-10', DATE '2025-06-10', 1.00);

INSERT INTO leave (leave_id, leave_type, document_id, start_date, end_date, leave_cnt)
VALUES (leave_seq.NEXTVAL, 2, 2, DATE '2025-06-11', DATE '2025-06-11', 0.50);




---------------------------------업무관리------------------------

DELETE FROM mail_attachment;
DELETE FROM mail_receiver;
DELETE FROM mail;
DELETE FROM reservation_participant;
DELETE FROM room_reservation;
DELETE FROM meeting_room;
DELETE FROM schedule;
DELETE FROM holidays;



-- 1. 공휴일

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-01-01', '신정', 0);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-01-28', '설날', 0);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-01-29', '설날 연휴', 0);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-01-30', '설날 연휴', 0);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-03-01', '삼일절', 0);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-05-05', '어린이날', 0);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-05-06', '어린이날 대체공휴일', 1);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-06-06', '현충일', 0);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-08-15', '광복절', 0);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-10-03', '개천절', 0);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-10-05', '추석', 0);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-10-06', '추석 연휴', 0);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-10-07', '추석 연휴', 0);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-10-09', '한글날', 0);

INSERT INTO holidays (holiday_id, holiday_year, holiday_date, holiday_name, is_substitute)
VALUES (holidays_seq.NEXTVAL, 2025, DATE '2025-12-25', '크리스마스', 0);


-- 2. 회의실

INSERT INTO meeting_room (room_id, room_name, location, status)
VALUES (meeting_room_seq.NEXTVAL, '회의실1', '본사 3층', 'ACTIVE');

INSERT INTO meeting_room (room_id, room_name, location, status)
VALUES (meeting_room_seq.NEXTVAL, '회의실2', '본사 2층', 'ACTIVE');

INSERT INTO meeting_room (room_id, room_name, location, status)
VALUES (meeting_room_seq.NEXTVAL, '회의실3', '본사 2층', 'ACTIVE');

INSERT INTO meeting_room (room_id, room_name, location, status)
VALUES (meeting_room_seq.NEXTVAL, '회의실4', '본사 5층', 'ACTIVE');

INSERT INTO meeting_room (room_id, room_name, location, status)
VALUES (meeting_room_seq.NEXTVAL, '회의실5', '본사 4층', 'ACTIVE');

INSERT INTO meeting_room (room_id, room_name, location, status)
VALUES (meeting_room_seq.NEXTVAL, '회의실6', '본사 1층', 'ACTIVE');



-- 3. 회의실 예약

INSERT INTO room_reservation (reservation_id, room_id, user_id, title, rsv_date, start_time, end_time, status, purpose, create_at)
VALUES (room_reservation_seq.NEXTVAL, 2, 2, '신규입사자 면접', DATE '2025-06-12', '10:00', '11:00', 'CONFIRMED', '신규입사자 최종 면접', SYSTIMESTAMP);

INSERT INTO room_reservation (reservation_id, room_id, user_id, title, rsv_date, start_time, end_time, status, purpose, create_at)
VALUES (room_reservation_seq.NEXTVAL, 4, 1, '경영진 월간 보고', DATE '2025-06-15', '14:00', '16:00', 'CONFIRMED', '6월 경영현황 보고', SYSTIMESTAMP);

INSERT INTO room_reservation (reservation_id, room_id, user_id, title, rsv_date, start_time, end_time, status, purpose, create_at)
VALUES (room_reservation_seq.NEXTVAL, 1, 7, '현장 안전교육', DATE '2025-06-18', '09:00', '12:00', 'CONFIRMED', '하반기 현장 안전교육', SYSTIMESTAMP);

INSERT INTO room_reservation (reservation_id, room_id, user_id, title, rsv_date, start_time, end_time, status, purpose, create_at)
VALUES (room_reservation_seq.NEXTVAL, 3, 4, '근태시스템 교육', DATE '2025-06-20', '13:00', '14:00', 'CANCELLED', '근태 시스템 사용자 교육', SYSTIMESTAMP);

INSERT INTO room_reservation (reservation_id, room_id, user_id, title, rsv_date, start_time, end_time, status, purpose, create_at)
VALUES (room_reservation_seq.NEXTVAL, 2, 3, '팀 주간 회의', DATE '2025-06-23', '09:00', '10:00', 'CONFIRMED', '주간 업무 보고', SYSTIMESTAMP);



-- 4. 예약 참석자

-- 예약1: 인사팀장(2) 주최, 인사실무자(3) 참석
INSERT INTO reservation_participant (participant_id, reservation_id, user_id, is_organizer)
VALUES (reservation_participant_seq.NEXTVAL, 1, 2, 1);

INSERT INTO reservation_participant (participant_id, reservation_id, user_id, is_organizer)
VALUES (reservation_participant_seq.NEXTVAL, 1, 3, 0);

-- 예약2: 대표이사(1) 주최, 인사팀장(2), 공사관리팀장(7), 안전관리팀장(8) 참석
INSERT INTO reservation_participant (participant_id, reservation_id, user_id, is_organizer)
VALUES (reservation_participant_seq.NEXTVAL, 2, 1, 1);

INSERT INTO reservation_participant (participant_id, reservation_id, user_id, is_organizer)
VALUES (reservation_participant_seq.NEXTVAL, 2, 2, 0);

INSERT INTO reservation_participant (participant_id, reservation_id, user_id, is_organizer)
VALUES (reservation_participant_seq.NEXTVAL, 2, 7, 0);

INSERT INTO reservation_participant (participant_id, reservation_id, user_id, is_organizer)
VALUES (reservation_participant_seq.NEXTVAL, 2, 8, 0);

-- 예약3: 공사관리팀장(7) 주최, 안전관리팀장(8), 일반직원1(9), 일반직원2(10) 참석
INSERT INTO reservation_participant (participant_id, reservation_id, user_id, is_organizer)
VALUES (reservation_participant_seq.NEXTVAL, 3, 7, 1);

INSERT INTO reservation_participant (participant_id, reservation_id, user_id, is_organizer)
VALUES (reservation_participant_seq.NEXTVAL, 3, 8, 0);

INSERT INTO reservation_participant (participant_id, reservation_id, user_id, is_organizer)
VALUES (reservation_participant_seq.NEXTVAL, 3, 9, 0);

INSERT INTO reservation_participant (participant_id, reservation_id, user_id, is_organizer)
VALUES (reservation_participant_seq.NEXTVAL, 3, 10, 0);

-- 예약5: 인사실무자(3) 주최, 근태담당자(4) 참석
INSERT INTO reservation_participant (participant_id, reservation_id, user_id, is_organizer)
VALUES (reservation_participant_seq.NEXTVAL, 5, 3, 1);

INSERT INTO reservation_participant (participant_id, reservation_id, user_id, is_organizer)
VALUES (reservation_participant_seq.NEXTVAL, 5, 4, 0);



-- 5. 사내메일

INSERT INTO mail (mail_id, user_id, parent_mail_id, title, content, create_at)
VALUES (mail_seq.NEXTVAL, 1, NULL, '6월 전사 공지사항', '안녕하세요. 6월 전사 공지사항을 안내드립니다.', SYSTIMESTAMP);

INSERT INTO mail (mail_id, user_id, parent_mail_id, title, content, create_at)
VALUES (mail_seq.NEXTVAL, 2, NULL, '신규입사자 온보딩 준비 요청', '이번 주 신규입사자 온보딩 자료를 준비해주세요.', SYSTIMESTAMP);

INSERT INTO mail (mail_id, user_id, parent_mail_id, title, content, create_at)
VALUES (mail_seq.NEXTVAL, 3, 2, 'RE: 신규입사자 온보딩 준비 요청', '네, 금요일까지 준비하겠습니다.', SYSTIMESTAMP);

INSERT INTO mail (mail_id, user_id, parent_mail_id, title, content, create_at)
VALUES (mail_seq.NEXTVAL, 7, NULL, '현장 안전점검 일정 조율', '이번 달 현장 안전점검 일정을 조율하고자 합니다.', SYSTIMESTAMP);

INSERT INTO mail (mail_id, user_id, parent_mail_id, title, content, create_at)
VALUES (mail_seq.NEXTVAL, 8, 4, 'RE: 현장 안전점검 일정 조율', '6월 25일로 잡는 게 좋을 것 같습니다.', SYSTIMESTAMP);

INSERT INTO mail (mail_id, user_id, parent_mail_id, title, content, create_at)
VALUES (mail_seq.NEXTVAL, 5, NULL, '6월 급여 처리 일정 안내', '6월 급여 처리는 25일 진행 예정입니다.', SYSTIMESTAMP);



-- 6. 메일 수신자

-- 메일1 수신: 인사팀장(2), 공사관리팀장(7), 안전관리팀장(8)
INSERT INTO mail_receiver (receiver_id, mail_id, user_id, is_read, is_deleted)
VALUES (mail_receiver_seq.NEXTVAL, 1, 2, 1, 0);

INSERT INTO mail_receiver (receiver_id, mail_id, user_id, is_read, is_deleted)
VALUES (mail_receiver_seq.NEXTVAL, 1, 7, 1, 0);

INSERT INTO mail_receiver (receiver_id, mail_id, user_id, is_read, is_deleted)
VALUES (mail_receiver_seq.NEXTVAL, 1, 8, 0, 0);

-- 메일2 수신: 인사실무자(3)
INSERT INTO mail_receiver (receiver_id, mail_id, user_id, is_read, is_deleted)
VALUES (mail_receiver_seq.NEXTVAL, 2, 3, 1, 0);

-- 메일3 수신: 인사팀장(2)
INSERT INTO mail_receiver (receiver_id, mail_id, user_id, is_read, is_deleted)
VALUES (mail_receiver_seq.NEXTVAL, 3, 2, 1, 0);

-- 메일4 수신: 안전관리팀장(8)
INSERT INTO mail_receiver (receiver_id, mail_id, user_id, is_read, is_deleted)
VALUES (mail_receiver_seq.NEXTVAL, 4, 8, 1, 0);

-- 메일5 수신: 공사관리팀장(7)
INSERT INTO mail_receiver (receiver_id, mail_id, user_id, is_read, is_deleted)
VALUES (mail_receiver_seq.NEXTVAL, 5, 7, 0, 0);

-- 메일6 수신: 인사팀장(2)
INSERT INTO mail_receiver (receiver_id, mail_id, user_id, is_read, is_deleted)
VALUES (mail_receiver_seq.NEXTVAL, 6, 2, 0, 0);



-- 7. 첨부파일

INSERT INTO mail_attachment (attachment_id, mail_id, original_name, saved_name, file_path, file_size, file_type, create_at)
VALUES (mail_attachment_seq.NEXTVAL, 1, '6월공지사항.pdf', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf', '/uploads/mail/2025/06/', 204800, 'application/pdf', SYSTIMESTAMP);

INSERT INTO mail_attachment (attachment_id, mail_id, original_name, saved_name, file_path, file_size, file_type, create_at)
VALUES (mail_attachment_seq.NEXTVAL, 4, '안전점검_체크리스트.xlsx', 'b2c3d4e5-f6a7-8901-bcde-f12345678901.xlsx', '/uploads/mail/2025/06/', 51200, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', SYSTIMESTAMP);

INSERT INTO mail_attachment (attachment_id, mail_id, original_name, saved_name, file_path, file_size, file_type, create_at)
VALUES (mail_attachment_seq.NEXTVAL, 6, '6월급여처리일정.docx', 'c3d4e5f6-a7b8-9012-cdef-123456789012.docx', '/uploads/mail/2025/06/', 32768, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', SYSTIMESTAMP);



-- 8. 일정

INSERT INTO schedule (schedule_id, user_id, dept_id, title, schedule_type, start_dt, end_dt, location, memo, created_at)
VALUES (schedule_seq.NEXTVAL, 2, NULL, '외부 채용박람회 참가', 'PERSONAL', DATE '2025-06-14', DATE '2025-06-14', '코엑스', '채용박람회 부스 운영', SYSTIMESTAMP);

INSERT INTO schedule (schedule_id, user_id, dept_id, title, schedule_type, start_dt, end_dt, location, memo, created_at)
VALUES (schedule_seq.NEXTVAL, 2, 4, '인사팀 월간 회의', 'DEPT', DATE '2025-06-16', DATE '2025-06-16', '소회의실A', '6월 인사팀 월간 업무 보고', SYSTIMESTAMP);

INSERT INTO schedule (schedule_id, user_id, dept_id, title, schedule_type, start_dt, end_dt, location, memo, created_at)
VALUES (schedule_seq.NEXTVAL, 1, NULL, '창립기념일', 'COMPANY', DATE '2025-07-01', DATE '2025-07-01', '전사', '회사 창립 15주년 기념행사', SYSTIMESTAMP);

INSERT INTO schedule (schedule_id, user_id, dept_id, title, schedule_type, start_dt, end_dt, location, memo, created_at)
VALUES (schedule_seq.NEXTVAL, 7, NULL, 'A현장 공사 착공', 'PROJECT', DATE '2025-06-23', DATE '2025-08-31', 'A현장', '1단계 골조공사 일정', SYSTIMESTAMP);

INSERT INTO schedule (schedule_id, user_id, dept_id, title, schedule_type, start_dt, end_dt, location, memo, created_at)
VALUES (schedule_seq.NEXTVAL, 4, NULL, '근태시스템 교육 수강', 'PERSONAL', DATE '2025-06-25', DATE '2025-06-26', '교육실', '신규 근태시스템 사용자 교육', SYSTIMESTAMP);

INSERT INTO schedule (schedule_id, user_id, dept_id, title, schedule_type, start_dt, end_dt, location, memo, created_at)
VALUES (schedule_seq.NEXTVAL, 7, 3, '하반기 현장 안전교육', 'DEPT', DATE '2025-06-18', DATE '2025-06-18', '대회의실', '전 현장 직원 안전교육 필수 참석', SYSTIMESTAMP);




----------------------------급여-----------------------------

-- 1. 급여 내역 테이블 샘플 데이터 삽입
INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 1, DATE '2026-05-14', 6846000, 760295, 6085705, 'Paid', CURRENT_TIMESTAMP);


-- 2. 급여 상세 항목 테이블 샘플 데이터 삽입
INSERT INTO payroll_details (payroll_details_id, payroll_id, item_type, item_id, item_name, amount, created_at)
VALUES (payroll_details_seq.NEXTVAL, 1, '수당', 2, '식대', 100000, CURRENT_TIMESTAMP);

INSERT INTO payroll_details (payroll_details_id, payroll_id, item_type, item_id, item_name, amount, created_at)
VALUES (payroll_details_seq.NEXTVAL, 1, '수당', 3, '교통비', 100000, CURRENT_TIMESTAMP);

INSERT INTO payroll_details (payroll_details_id, payroll_id, item_type, item_id, item_name, amount, created_at)
VALUES (payroll_details_seq.NEXTVAL, 1, '공제', 5, '국민연금', 265500, CURRENT_TIMESTAMP);

INSERT INTO payroll_details (payroll_details_id, payroll_id, item_type, item_id, item_name, amount, created_at)
VALUES (payroll_details_seq.NEXTVAL, 1, '공제', 6, '건강보험', 235601, CURRENT_TIMESTAMP);

INSERT INTO payroll_details (payroll_details_id, payroll_id, item_type, item_id, item_name, amount, created_at)
VALUES (payroll_details_seq.NEXTVAL, 1, '공제', 7, '고용보험', 59814, CURRENT_TIMESTAMP);

INSERT INTO payroll_details (payroll_details_id, payroll_id, item_type, item_id, item_name, amount, created_at)
VALUES (payroll_details_seq.NEXTVAL, 1, '공제', 8, '소득세', 199380, CURRENT_TIMESTAMP);


-- 3. 급여명세서 테이블 샘플 데이터 삽입
INSERT INTO payslips (payslip_id, payroll_id, file_name, file_path, file_type, created_at)
VALUES (payslips_seq.NEXTVAL, 1, '급여명세서_2026년5월.pdf', 'C:\Users\user\Desktop\downloads', 'PDF', CURRENT_TIMESTAMP);


--4. 기본급 정보 테이블 샘플 데이터 삽입
INSERT INTO salary (salary_id, employee_id, base_salary, created_at)
VALUES (salary_seq.NEXTVAL, 1, 6646000, CURRENT_TIMESTAMP);


--5. 수당 항목 테이블 샘플 데이터 삽입
INSERT INTO allowance_items (allowance_item_id, item_name, amount)
VALUES (2, '식대', 100000);

INSERT INTO allowance_items (allowance_item_id, item_name, amount)
VALUES (3, '교통비', 100000);


--6. 직원 수당 정보 테이블 샘플 데이터 삽입
INSERT INTO employee_allowance (employee_allowance_id, employee_id, allowance_item_id, amount)
VALUES (employee_allowance_seq.NEXTVAL, 1, 2, 100000);

INSERT INTO employee_allowance (employee_allowance_id, employee_id, allowance_item_id, amount)
VALUES (employee_allowance_seq.NEXTVAL, 1, 3, 100000);


--7. 공제 항목 테이블 샘플 데이터 삽입
INSERT INTO deduction_items (deduction_item_id, item_name, ratio)
VALUES (5, '국민연금', 0.045);

INSERT INTO deduction_items (deduction_item_id, item_name, ratio)
VALUES (6, '건강보험', 0.03545);

INSERT INTO deduction_items (deduction_item_id, item_name, ratio)
VALUES (7, '고용보험', 0.009);

INSERT INTO deduction_items (deduction_item_id, item_name, ratio)
VALUES (8, '소득세', 0.07);


--8. 직원 공제 정보 테이블 샘플 데이터 삽입
INSERT INTO employee_deduction (employee_deduction_id, employee_id, deduction_item_id, amount)
VALUES (employee_deduction_seq.NEXTVAL, 1, 5, 265500);

INSERT INTO employee_deduction (employee_deduction_id, employee_id, deduction_item_id, amount)
VALUES (employee_deduction_seq.NEXTVAL, 1, 6, 235601);

INSERT INTO employee_deduction (employee_deduction_id, employee_id, deduction_item_id, amount)
VALUES (employee_deduction_seq.NEXTVAL, 1, 7, 59814);

INSERT INTO employee_deduction (employee_deduction_id, employee_id, deduction_item_id, amount)
VALUES (employee_deduction_seq.NEXTVAL, 1, 8, 199380);


--9. 부양가족 / 원천징수 테이블 샘플 데이터 삽입
INSERT INTO employee_tax_info (tax_info_id, employee_id, dependents, children, withholding_rate, created_at)
VALUES (employee_tax_info_seq.NEXTVAL, 1, 1, 0, 1, CURRENT_TIMESTAMP);

COMMIT;
