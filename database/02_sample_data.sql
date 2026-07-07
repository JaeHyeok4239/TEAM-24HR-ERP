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
VALUES ( role_seq.NEXTVAL, 'HR', '인사', '인사관리 메뉴 접근 권한' );

INSERT INTO roles ( role_id, role_code, role_name, description )
VALUES ( role_seq.NEXTVAL, 'HR_LEAD', '인사 책임자', '인사/근태/급여/권한 총괄 추가 관리 권한' );

INSERT INTO roles ( role_id, role_code, role_name, description )
VALUES ( role_seq.NEXTVAL, 'ATTENDANCE', '근태', '근태관리 메뉴 접근 권한' );

INSERT INTO roles ( role_id, role_code, role_name, description )
VALUES ( role_seq.NEXTVAL, 'PAYROLL', '급여', '급여관리 메뉴 접근 권한' );


-- 4. 사용자 테이블 샘플 데이터

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP001', 'ceo', '1234', '대표이사', 1, 8, 'REGULAR', CURRENT_TIMESTAMP );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP002', 'hr_lead', '1234', '인사팀장', 2, 6, 'REGULAR', CURRENT_TIMESTAMP );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, department_id, position_id, employment_type, hire_date )
VALUES ( employee_seq.NEXTVAL, 'EMP003', 'hr', '1234', '인사실무자', 2, 3, 'REGULAR', CURRENT_TIMESTAMP );

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
VALUES ( user_role_seq.NEXTVAL, 1, 3 ); -- 대표이사 

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 1, 4 ); -- 대표이사

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 1, 5 ); -- 대표이사 

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 1, 6 ); -- 대표이사 

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 2, 2 ); -- 인사팀장 USER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 2, 3 ); -- 인사팀장 

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 2, 4 ); -- 인사팀장 

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 3, 2 ); -- 인사실무자 USER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 3, 3 ); -- 인사실무자 

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 4, 2 ); -- 근태담당자 USER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 4, 5 ); -- 근태담당자 

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 5, 2 ); -- 급여담당자 USER

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
VALUES ( user_role_seq.NEXTVAL, 5, 6 ); -- 급여담당자 

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

-- 직원별 연차 잔액 샘플 데이터
-- 정규직 employee_id 1~10만 등록
INSERT INTO annual_leave_balances ( annual_leave_balance_id, employee_id, leave_year, total_days, remaining_days, granted_at, expires_at )
VALUES ( annual_leave_balance_seq.NEXTVAL, 1, EXTRACT(YEAR FROM SYSDATE), 15.00, 15.00, SYSTIMESTAMP, ADD_MONTHS(SYSTIMESTAMP, 12) );

INSERT INTO annual_leave_balances ( annual_leave_balance_id, employee_id, leave_year, total_days, remaining_days, granted_at, expires_at )
VALUES ( annual_leave_balance_seq.NEXTVAL, 2, EXTRACT(YEAR FROM SYSDATE), 15.00, 15.00, SYSTIMESTAMP, ADD_MONTHS(SYSTIMESTAMP, 12) );

INSERT INTO annual_leave_balances ( annual_leave_balance_id, employee_id, leave_year, total_days, remaining_days, granted_at, expires_at )
VALUES ( annual_leave_balance_seq.NEXTVAL, 3, EXTRACT(YEAR FROM SYSDATE), 15.00, 15.00, SYSTIMESTAMP, ADD_MONTHS(SYSTIMESTAMP, 12) );

INSERT INTO annual_leave_balances ( annual_leave_balance_id, employee_id, leave_year, total_days, remaining_days, granted_at, expires_at )
VALUES ( annual_leave_balance_seq.NEXTVAL, 4, EXTRACT(YEAR FROM SYSDATE), 15.00, 15.00, SYSTIMESTAMP, ADD_MONTHS(SYSTIMESTAMP, 12) );

INSERT INTO annual_leave_balances ( annual_leave_balance_id, employee_id, leave_year, total_days, remaining_days, granted_at, expires_at )
VALUES ( annual_leave_balance_seq.NEXTVAL, 5, EXTRACT(YEAR FROM SYSDATE), 15.00, 15.00, SYSTIMESTAMP, ADD_MONTHS(SYSTIMESTAMP, 12) );

INSERT INTO annual_leave_balances ( annual_leave_balance_id, employee_id, leave_year, total_days, remaining_days, granted_at, expires_at )
VALUES ( annual_leave_balance_seq.NEXTVAL, 6, EXTRACT(YEAR FROM SYSDATE), 15.00, 15.00, SYSTIMESTAMP, ADD_MONTHS(SYSTIMESTAMP, 12) );

INSERT INTO annual_leave_balances ( annual_leave_balance_id, employee_id, leave_year, total_days, remaining_days, granted_at, expires_at )
VALUES ( annual_leave_balance_seq.NEXTVAL, 7, EXTRACT(YEAR FROM SYSDATE), 15.00, 15.00, SYSTIMESTAMP, ADD_MONTHS(SYSTIMESTAMP, 12) );

INSERT INTO annual_leave_balances ( annual_leave_balance_id, employee_id, leave_year, total_days, remaining_days, granted_at, expires_at )
VALUES ( annual_leave_balance_seq.NEXTVAL, 8, EXTRACT(YEAR FROM SYSDATE), 15.00, 15.00, SYSTIMESTAMP, ADD_MONTHS(SYSTIMESTAMP, 12) );

INSERT INTO annual_leave_balances ( annual_leave_balance_id, employee_id, leave_year, total_days, remaining_days, granted_at, expires_at )
VALUES ( annual_leave_balance_seq.NEXTVAL, 9, EXTRACT(YEAR FROM SYSDATE), 15.00, 15.00, SYSTIMESTAMP, ADD_MONTHS(SYSTIMESTAMP, 12) );

INSERT INTO annual_leave_balances ( annual_leave_balance_id, employee_id, leave_year, total_days, remaining_days, granted_at, expires_at )
VALUES ( annual_leave_balance_seq.NEXTVAL, 10, EXTRACT(YEAR FROM SYSDATE), 15.00, 15.00, SYSTIMESTAMP, ADD_MONTHS(SYSTIMESTAMP, 12) );

-- 테이블 삭제
DELETE FROM attendance_logs;
DELETE FROM attendance_results;
DELETE FROM attendance_time_policies;
DELETE FROM attendance_thresholds;
DELETE FROM workplaces;

-- 근무 시간 규칙
-- 출퇴근, 점심시간
INSERT INTO attendance_time_policies VALUES (attendance_time_policies_seq.NEXTVAL, 'REGULAR', 'WORK', 900, 1800, CURRENT_TIMESTAMP, NULL);
INSERT INTO attendance_time_policies VALUES (attendance_time_policies_seq.NEXTVAL, 'REGULAR', 'BREAK', 1200, 1300, CURRENT_TIMESTAMP, NULL);

-- 근태 판정 기준
INSERT INTO attendance_thresholds
VALUES (attendance_thresholds_seq.NEXTVAL, 'REGULAR', 'LATE', 0, '출근 시간 초과 시 지각', CURRENT_TIMESTAMP, NULL);

INSERT INTO attendance_thresholds
VALUES (attendance_thresholds_seq.NEXTVAL, 'REGULAR', 'EARLY_LEAVE', 0, '퇴근 시간 미달 시 조퇴', CURRENT_TIMESTAMP, NULL);

INSERT INTO attendance_thresholds
VALUES (attendance_thresholds_seq.NEXTVAL, 'REGULAR', 'ABSENCE', 180, '출근 시간 기준 3시간 초과(오후 12시) 출근 시 결근', CURRENT_TIMESTAMP, NULL);

-- 근무지
-- 정규직 근무지
INSERT INTO workplaces VALUES (workplaces_seq.NEXTVAL, 'HQ', '본사', '강남역', 100, 37.4979420, 127.0276210, CURRENT_TIMESTAMP, NULL);
-- 일용직 근무지
INSERT INTO workplaces VALUES (workplaces_seq.NEXTVAL, 'TEMP01', '근무지1', '부산광역시 강서구', 100, 35.1052000, 128.8450000, CURRENT_TIMESTAMP, NULL);
INSERT INTO workplaces VALUES (workplaces_seq.NEXTVAL, 'TEMP02', '근무지2', '대전광역시 유성구', 100, 36.3350000, 127.3350000, CURRENT_TIMESTAMP, NULL);

-- 전자결재 샘플 데이터(테스트 용도로만 사용)
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

-- ------------------------------------------------------------
-- 1. 문서 유형 (document_type)
-- ------------------------------------------------------------
INSERT INTO document_type (type_id, type_name, detail_table, required_processing)
VALUES (1, '연차신청서', 'leave', 'N');

INSERT INTO document_type (type_id, type_name, detail_table, required_processing)
VALUES (2, '지출결의서', 'expenditure', 'Y');

INSERT INTO document_type (type_id, type_name, detail_table, required_processing)
VALUES (3, '구매요청서', 'purchase', 'Y');

-- detail_table이 없는 유형 -> 서비스 로직상 schema 등록 불가 (참고용, INSERT 없음)
INSERT INTO document_type (type_id, type_name, detail_table, required_processing)
VALUES (4, '재직증명서', NULL, 'N');


-- ------------------------------------------------------------
-- 2. 문서유형 스키마 (document_type_schema)
--    detail_table이 있는 1,2,3번 유형만 등록
-- ------------------------------------------------------------
INSERT INTO document_type_schema (schema_id, schema_json, document_type, created_at, updated_at)
VALUES (
    1,
    '{"fields":[
        {"name":"leaveType","type":"select","required":true,"options":["연차","반차","조퇴"]},
        {"name":"startDate","type":"date","required":true},
        {"name":"endDate","type":"date","required":true},
        {"name":"reason","type":"text","required":true}
    ]}',
    1, SYSTIMESTAMP, NULL
);

INSERT INTO document_type_schema (schema_id, schema_json, document_type, created_at, updated_at)
VALUES (
    2,
    '{"fields":[
        {"name":"amount","type":"number","required":true},
        {"name":"category","type":"select","required":true,"options":["교통비","식비","비품비","기타"]},
        {"name":"description","type":"text","required":true}
    ]}',
    2, SYSTIMESTAMP, NULL
);

INSERT INTO document_type_schema (schema_id, schema_json, document_type, created_at, updated_at)
VALUES (
    3,
    '{"fields":[
        {"name":"itemName","type":"text","required":true},
        {"name":"quantity","type":"number","required":true},
        {"name":"unitPrice","type":"number","required":true},
        {"name":"vendor","type":"text","required":false}
    ]}',
    3, SYSTIMESTAMP, NULL
);


-- ------------------------------------------------------------
-- 3. 휴가 유형 (leave_type)
-- ------------------------------------------------------------
INSERT INTO leave_type (type_id, type_name, is_paid) VALUES (1, '연차', 'Y');
INSERT INTO leave_type (type_id, type_name, is_paid) VALUES (2, '반차', 'Y');
INSERT INTO leave_type (type_id, type_name, is_paid) VALUES (3, '조퇴', 'N');
INSERT INTO leave_type (type_id, type_name, is_paid) VALUES (4, '병가', 'N');


-- ------------------------------------------------------------
-- 4. 결재선 (approval_line)
--    사용 employee_id: 1=김민준(최종승인자), 11=한지민(1차승인), 6=강도윤(구매 1차승인)
-- ------------------------------------------------------------
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver, department_id)
VALUES (1, 1, 1, 11, NULL); -- 연차신청서 1단계

INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver, department_id)
VALUES (2, 1, 2, 1, NULL);  -- 연차신청서 2단계(최종)

INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver, department_id)
VALUES (3, 2, 1, 11, NULL); -- 지출결의서 1단계

INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver, department_id)
VALUES (4, 2, 2, 1, NULL);  -- 지출결의서 2단계(최종)

INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver, department_id)
VALUES (5, 3, 1, 6, NULL);  -- 구매요청서 1단계

INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver, department_id)
VALUES (6, 3, 2, 1, NULL);  -- 구매요청서 2단계(최종)


-- ------------------------------------------------------------
-- 5. 문서 처리 부서 (document_process)
--    required_processing='Y'인 지출결의서/구매요청서만 대상
--    (departments FK는 3=재무팀으로 가정)
-- ------------------------------------------------------------
INSERT INTO document_process (process_id, document_type, process_department)
VALUES (1, 2, 3);

INSERT INTO document_process (process_id, document_type, process_department)
VALUES (2, 3, 3);


-- ------------------------------------------------------------
-- 6. 결재 문서 (document)
--    기안자: 93=서하늘, 65=심가온, 41=김하준, 53=서도윤
-- ------------------------------------------------------------
-- 문서1: 서하늘 연차신청 - 1단계 결재 대기중
INSERT INTO document (document_id, document_type, requester_id, processor_id, document_title,
    status, current_step, created_at, updated_at, requested_at, processed_at, reject_reason,
    document_content, version, document_version)
VALUES (1, 1, 93, NULL, '서하늘 연차신청서', 'REQ', 1,
    TIMESTAMP '2026-07-01 09:10:00', NULL, TIMESTAMP '2026-07-01 09:10:00', NULL, NULL,
    '{"leaveType":"연차","startDate":"2026-07-10","endDate":"2026-07-10","reason":"개인 사유"}',
    0, 1);

-- 문서2: 심가온 반차신청 - 승인 완료(자동 처리, required_processing='N')
INSERT INTO document (document_id, document_type, requester_id, processor_id, document_title,
    status, current_step, created_at, updated_at, requested_at, processed_at, reject_reason,
    document_content, version, document_version)
VALUES (2, 1, 65, NULL, '심가온 반차신청서', 'COM', 2,
    TIMESTAMP '2026-06-20 08:40:00', TIMESTAMP '2026-06-20 14:00:00',
    TIMESTAMP '2026-06-20 08:40:00', TIMESTAMP '2026-06-20 14:00:00', NULL,
    '{"leaveType":"반차","startDate":"2026-06-21","endDate":"2026-06-21","reason":"병원 진료"}',
    2, 1);

-- 문서3: 김하준 지출결의서 - 결재 승인 완료 후 재무팀 처리중
INSERT INTO document (document_id, document_type, requester_id, processor_id, document_title,
    status, current_step, created_at, updated_at, requested_at, processed_at, reject_reason,
    document_content, version, document_version)
VALUES (3, 2, 41, 8, '김하준 지출결의서(교통비)', 'PRC', 2,
    TIMESTAMP '2026-06-25 10:00:00', TIMESTAMP '2026-06-25 10:05:00',
    TIMESTAMP '2026-06-25 10:00:00', NULL, NULL,
    '{"amount":45000,"category":"교통비","description":"현장 출장 교통비 정산"}',
    2, 1);

-- 문서4: 서도윤 구매요청서 - 1단계에서 반려
INSERT INTO document (document_id, document_type, requester_id, processor_id, document_title,
    status, current_step, created_at, updated_at, requested_at, processed_at, reject_reason,
    document_content, version, document_version)
VALUES (4, 3, 53, NULL, '서도윤 구매요청서(사무용품)', 'REJ', 1,
    TIMESTAMP '2026-06-28 11:00:00', TIMESTAMP '2026-06-28 15:00:00',
    TIMESTAMP '2026-06-28 11:00:00', TIMESTAMP '2026-06-28 15:00:00', '예산 초과로 반려',
    '{"itemName":"모니터","quantity":5,"unitPrice":250000,"vendor":"LG전자"}',
    1, 1);

-- 문서5: 김하준 연차신청서 - 임시저장(작성중)
INSERT INTO document (document_id, document_type, requester_id, processor_id, document_title,
    status, current_step, created_at, updated_at, requested_at, processed_at, reject_reason,
    document_content, version, document_version)
VALUES (5, 1, 41, NULL, '김하준 연차신청서(임시)', 'TMP', 1,
    TIMESTAMP '2026-07-05 09:00:00', NULL, NULL, NULL, NULL,
    '{"leaveType":"연차","startDate":"2026-07-20","endDate":"2026-07-21","reason":""}',
    0, 1);


-- ------------------------------------------------------------
-- 7. 결재 이력 (approval_history)
-- ------------------------------------------------------------
-- 문서1: 1단계 대기중
INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status,
    approver_comment, acted_at, created_at, version, document_version)
VALUES (1, 1, 1, 11, 'PND', NULL, NULL, TIMESTAMP '2026-07-01 09:10:00', 0, 1);

-- 문서2: 1,2단계 모두 승인
INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status,
    approver_comment, acted_at, created_at, version, document_version)
VALUES (2, 2, 1, 11, 'APR', '확인했습니다.', TIMESTAMP '2026-06-20 10:00:00',
    TIMESTAMP '2026-06-20 08:40:00', 1, 1);

INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status,
    approver_comment, acted_at, created_at, version, document_version)
VALUES (3, 2, 2, 1, 'APR', '승인합니다.', TIMESTAMP '2026-06-20 14:00:00',
    TIMESTAMP '2026-06-20 08:40:00', 1, 1);

-- 문서3: 1,2단계 모두 승인 (이후 재무팀 처리 단계로 이동)
INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status,
    approver_comment, acted_at, created_at, version, document_version)
VALUES (4, 3, 1, 11, 'APR', '결재 승인', TIMESTAMP '2026-06-25 10:03:00',
    TIMESTAMP '2026-06-25 10:00:00', 1, 1);

INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status,
    approver_comment, acted_at, created_at, version, document_version)
VALUES (5, 3, 2, 1, 'APR', '결재 승인', TIMESTAMP '2026-06-25 10:05:00',
    TIMESTAMP '2026-06-25 10:00:00', 1, 1);

-- 문서4: 1단계에서 반려, 2단계는 취소 처리
INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status,
    approver_comment, acted_at, created_at, version, document_version)
VALUES (6, 4, 1, 6, 'REJ', '예산 초과로 반려', TIMESTAMP '2026-06-28 15:00:00',
    TIMESTAMP '2026-06-28 11:00:00', 1, 1);

INSERT INTO approval_history (history_id, document_id, step_order, approver_id, status,
    approver_comment, acted_at, created_at, version, document_version)
VALUES (7, 4, 2, 1, 'CAN', NULL, NULL, TIMESTAMP '2026-06-28 11:00:00', 0, 1);


-- ------------------------------------------------------------
-- 8. 첨부파일 (attachment)
-- ------------------------------------------------------------
INSERT INTO attachment (attachment_id, original_name, stored_name, attachment_type,
    upload_time, attachment_size, uploader)
VALUES (1, '병원진료확인서.pdf', 'a1b2c3d4-1111.pdf', 'application/pdf',
    TIMESTAMP '2026-06-20 08:35:00', 245678, 65);

INSERT INTO attachment (attachment_id, original_name, stored_name, attachment_type,
    upload_time, attachment_size, uploader)
VALUES (2, '교통비영수증.jpg', 'a1b2c3d4-2222.jpg', 'image/jpeg',
    TIMESTAMP '2026-06-25 09:55:00', 1024500, 41);

INSERT INTO attachment (attachment_id, original_name, stored_name, attachment_type,
    upload_time, attachment_size, uploader)
VALUES (3, '모니터견적서.xlsx', 'a1b2c3d4-3333.xlsx',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    TIMESTAMP '2026-06-28 10:50:00', 87234, 53);


-- ------------------------------------------------------------
-- 9. 문서-첨부파일 매핑 (document_attach_mapping)
-- ------------------------------------------------------------
INSERT INTO document_attach_mapping (doc_mapping_id, document_id, attachment_id) VALUES (1, 2, 1);
INSERT INTO document_attach_mapping (doc_mapping_id, document_id, attachment_id) VALUES (2, 3, 2);
INSERT INTO document_attach_mapping (doc_mapping_id, document_id, attachment_id) VALUES (3, 4, 3);


-- ------------------------------------------------------------
-- 10. 결재 위임 (approval_delegate)
-- ------------------------------------------------------------
-- 현재 활성 위임: 한지민(11) -> 강도윤(6), 여름휴가 기간
INSERT INTO approval_delegate (approval_delegate_id, approver_id, delegate_id, start_date,
    end_date, reason, is_active, approval_line_id)
VALUES (1, 11, 6, DATE '2026-07-15', DATE '2026-07-19', '여름휴가로 인한 결재 위임', 'Y', 1);

-- 종료된 위임 이력
INSERT INTO approval_delegate (approval_delegate_id, approver_id, delegate_id, start_date,
    end_date, reason, is_active, approval_line_id)
VALUES (2, 1, 6, DATE '2026-05-01', DATE '2026-05-05', '출장으로 인한 결재 위임', 'N', 3);


-- ------------------------------------------------------------
-- 11. 휴가 신청 상세 (leave, leave_date)
-- ------------------------------------------------------------
INSERT INTO leave (leave_id, leave_type, document_id, leave_cnt, leave_reason)
VALUES (1, 1, 1, 1.0, '개인 사유로 인한 연차 사용');

INSERT INTO leave (leave_id, leave_type, document_id, leave_cnt, leave_reason)
VALUES (2, 2, 2, 0.5, '병원 진료로 인한 반차 사용');

INSERT INTO leave_date (leave_date_id, leave_id, leave_date) VALUES (1, 1, DATE '2026-07-10');
INSERT INTO leave_date (leave_date_id, leave_id, leave_date) VALUES (2, 2, DATE '2026-06-21');

---------------------------------업무관리------------------------

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



-- 5. 일정

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
VALUES (payrolls_seq.NEXTVAL, 1, '2026-05', 8038000, 849039, 7188961, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 2, '2026-05', 5266000, 605134, 4660866, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 3, '2026-05', 6846000, 760295, 6085705, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 4, '2026-05', 7028000, 773845, 6254155, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 5, '2026-05', 6254155, 561893, 4342107, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 6, '2026-05', 4698000, 537286, 4160714, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 7, '2026-05', 4332000, 493567, 3838433, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 8, '2026-05', 5190000, 596056, 4593944, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 9, '2026-05', 5249000, 603103, 4645897, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 10, '2026-05', 4541000, 518532, 4022468, 'Paid', CURRENT_TIMESTAMP);


INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 1, '2026-06', 8038000, 849039, 7188961, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 2, '2026-06', 5266000, 605134, 4660866, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 3, '2026-06', 6846000, 760295, 6085705, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 4, '2026-06', 7028000, 773845, 6254155, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 5, '2026-06', 4904000, 561893, 4342107, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 6, '2026-06', 4759000, 544573, 4214427, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 7, '2026-06', 4332000, 493567, 3838433, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 8, '2026-06', 5062000, 580766, 4481234, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 9, '2026-06', 5223000, 599997, 4623003, 'Paid', CURRENT_TIMESTAMP);

INSERT INTO payrolls (payroll_id, employee_id, pay_month, total_pay, total_deduction, net_salary, status, created_at)
VALUES (payrolls_seq.NEXTVAL, 10, '2026-06', 4439000, 506349, 3932651, 'Paid', CURRENT_TIMESTAMP);


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



--3. 기본급 정보 테이블 샘플 데이터 삽입
INSERT INTO salary (salary_id, employee_id, base_salary, created_at)
VALUES (salary_seq.NEXTVAL, 1, 6646000, CURRENT_TIMESTAMP);


COMMIT;
