-- 0. 데이터 삭제 순서

DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM positions;
DELETE FROM departments;

-- 1. 부서 테이블 샘플 데이터

INSERT INTO departments (department_id, department_code, department_name, description)
VALUES (department_seq.NEXTVAL, 'EXEC', '경영지원팀', '전사 운영 및 경영지원 업무를 담당하는 부서');

INSERT INTO departments (department_id, department_code, department_name, description)
VALUES (department_seq.NEXTVAL, 'HR', '인사팀', '인사정보, 근태, 채용 및 인사 운영을 담당하는 부서');

INSERT INTO departments (department_id, department_code, department_name, description)
VALUES (department_seq.NEXTVAL, 'FIN', '재무회계팀', '급여, 회계, 정산 및 재무 업무를 담당하는 부서');

INSERT INTO departments (department_id, department_code, department_name, description)
VALUES (department_seq.NEXTVAL, 'PUR', '구매팀', '자재 구매 및 협력업체 관리를 담당하는 부서');

INSERT INTO departments (department_id, department_code, department_name, description)
VALUES (department_seq.NEXTVAL, 'CONST', '공사관리팀', '건설 현장 및 공사 일정 관리를 담당하는 부서');

INSERT INTO departments (department_id, department_code, department_name, description)
VALUES (department_seq.NEXTVAL, 'SAFE', '안전관리팀', '현장 안전점검 및 산업안전 관리를 담당하는 부서');


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
