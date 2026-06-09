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