-- 0. 데이터 삭제 순서

DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM positions;
DELETE FROM departments;

-- 1. 부서 테이블 샘플 데이터

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( department_seq.NEXTVAL, 'CEO', '대표이사', NULL, '회사 최고 경영자' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( department_seq.NEXTVAL, 'MGMT', '경영지원본부', ( SELECT department_id FROM departments WHERE department_code = 'CEO' ), '경영지원 업무 총괄' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( department_seq.NEXTVAL, 'FIELD', '현장관리본부', ( SELECT department_id FROM departments WHERE department_code = 'CEO' ), '현장 운영 및 공사 관리 총괄' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( department_seq.NEXTVAL, 'HR', '인사팀', ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), '인사 및 채용 담당' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( department_seq.NEXTVAL, 'FIN', '재무회계팀', ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), '재무 및 회계 담당' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( department_seq.NEXTVAL, 'PUR', '구매팀', ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), '자재 구매 및 협력업체 관리' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( department_seq.NEXTVAL, 'DEV', '개발팀', ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), '사내 시스템 개발 및 유지보수 담당' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( department_seq.NEXTVAL, 'CONST', '공사관리팀', ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), '공사 일정 및 현장 관리' );

INSERT INTO departments ( department_id, department_code, department_name, parent_department_id, description )
VALUES ( department_seq.NEXTVAL, 'SAFE', '안전관리팀', ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), '산업안전 및 현장 안전관리' );

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

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210001', 'kimminjun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '김민준', '010-9001-0001', 'kimminjun@24hr.example.com', '04524', '서울특별시 중구 세종대로 110', '24HR타워 1층', '하나은행', NULL, '김민준', NULL, ( SELECT department_id FROM departments WHERE department_code = 'CEO' ), ( SELECT position_id FROM positions WHERE position_code = 'CEO' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-01-08 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210002', 'leeseyeon', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '이서연', '010-9002-0002', 'leeseyeon@24hr.example.com', '06236', '서울특별시 강남구 테헤란로 152', '업무동 2층', '하나은행', NULL, '이서연', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIN' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-02-15 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210003', 'parkjihoon', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '박지훈', '010-9003-0003', 'parkjihoon@24hr.example.com', '03186', '서울특별시 종로구 종로 51', '본관 3층', '하나은행', NULL, '박지훈', NULL, ( SELECT department_id FROM departments WHERE department_code = 'PUR' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-03-22 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210004', 'choiyujin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '최유진', '010-9004-0004', 'choiyujin@24hr.example.com', '07242', '서울특별시 영등포구 은행로 30', '사무동 4층', '하나은행', NULL, '최유진', NULL, ( SELECT department_id FROM departments WHERE department_code = 'DEV' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-04-05 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210005', 'junghaneul', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '정하늘', '010-9005-0005', 'junghaneul@24hr.example.com', '16514', '경기도 수원시 영통구 광교로 156', '현장사무소 5층', '하나은행', NULL, '정하늘', NULL, ( SELECT department_id FROM departments WHERE department_code = 'CONST' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-05-12 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210006', 'kangdoyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '강도윤', '010-9006-0006', 'kangdoyun@24hr.example.com', '13529', '경기도 성남시 분당구 판교역로 235', '분당오피스 6층', '하나은행', NULL, '강도윤', NULL, ( SELECT department_id FROM departments WHERE department_code = 'SAFE' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-06-19 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210007', 'josubin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '조수빈', '010-9007-0007', 'josubin@24hr.example.com', '48059', '부산광역시 해운대구 센텀중앙로 90', '부산지사 7층', '하나은행', NULL, '조수빈', NULL, ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-07-02 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210008', 'yoonjiho', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '윤지호', '010-9008-0008', 'yoonjiho@24hr.example.com', '35209', '대전광역시 서구 둔산대로 100', '대전지사 8층', '하나은행', NULL, '윤지호', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-08-09 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210009', 'jangseojun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '장서준', '010-9009-0009', 'jangseojun@24hr.example.com', '41911', '대구광역시 중구 국채보상로 586', '대구지사 9층', '하나은행', NULL, '장서준', NULL, ( SELECT department_id FROM departments WHERE department_code = 'HR' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-09-16 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210010', 'imnayeon', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '임나연', '010-9000-0010', 'imnayeon@24hr.example.com', '61945', '광주광역시 서구 상무중앙로 7', '광주지사 10층', '하나은행', NULL, '임나연', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIN' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-10-23 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210011', 'hanjimin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '한지민', '010-9001-0011', 'hanjimin@24hr.example.com', '04524', '서울특별시 중구 세종대로 110', '24HR타워 11층', '하나은행', NULL, '한지민', NULL, ( SELECT department_id FROM departments WHERE department_code = 'PUR' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-11-06 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210012', 'ohhyunwoo', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '오현우', '010-9002-0012', 'ohhyunwoo@24hr.example.com', '06236', '서울특별시 강남구 테헤란로 152', '업무동 12층', '하나은행', NULL, '오현우', NULL, ( SELECT department_id FROM departments WHERE department_code = 'DEV' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-12-13 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220001', 'seominjae', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '서민재', '010-9003-0013', 'seominjae@24hr.example.com', '03186', '서울특별시 종로구 종로 51', '본관 13층', '하나은행', NULL, '서민재', NULL, ( SELECT department_id FROM departments WHERE department_code = 'CONST' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-01-20 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220002', 'shinyeeun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '신예은', '010-9004-0014', 'shinyeeun@24hr.example.com', '07242', '서울특별시 영등포구 은행로 30', '사무동 14층', '하나은행', NULL, '신예은', NULL, ( SELECT department_id FROM departments WHERE department_code = 'SAFE' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-02-03 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220003', 'kwontaehyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '권태현', '010-9005-0015', 'kwontaehyun@24hr.example.com', '16514', '경기도 수원시 영통구 광교로 156', '현장사무소 15층', '하나은행', NULL, '권태현', NULL, ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-03-10 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220004', 'hwangseoa', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '황서아', '010-9006-0016', 'hwangseoa@24hr.example.com', '13529', '경기도 성남시 분당구 판교역로 235', '분당오피스 16층', '하나은행', NULL, '황서아', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-04-17 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220005', 'anjiwoo', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '안지우', '010-9007-0017', 'anjiwoo@24hr.example.com', '48059', '부산광역시 해운대구 센텀중앙로 90', '부산지사 17층', '하나은행', NULL, '안지우', NULL, ( SELECT department_id FROM departments WHERE department_code = 'HR' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-05-24 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220006', 'songjunho', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '송준호', '010-9008-0018', 'songjunho@24hr.example.com', '35209', '대전광역시 서구 둔산대로 100', '대전지사 18층', '하나은행', NULL, '송준호', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIN' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-06-07 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220007', 'ryuharin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '류하린', '010-9009-0019', 'ryuharin@24hr.example.com', '41911', '대구광역시 중구 국채보상로 586', '대구지사 19층', '하나은행', NULL, '류하린', NULL, ( SELECT department_id FROM departments WHERE department_code = 'PUR' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-07-14 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220008', 'hongminseo', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '홍민서', '010-9000-0020', 'hongminseo@24hr.example.com', '61945', '광주광역시 서구 상무중앙로 7', '광주지사 20층', '하나은행', NULL, '홍민서', NULL, ( SELECT department_id FROM departments WHERE department_code = 'DEV' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-08-21 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220009', 'baesungmin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '배성민', '010-9001-0021', 'baesungmin@24hr.example.com', '04524', '서울특별시 중구 세종대로 110', '24HR타워 1층', '하나은행', NULL, '배성민', NULL, ( SELECT department_id FROM departments WHERE department_code = 'CONST' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-09-04 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220010', 'moonchaewon', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '문채원', '010-9002-0022', 'moonchaewon@24hr.example.com', '06236', '서울특별시 강남구 테헤란로 152', '업무동 2층', '하나은행', NULL, '문채원', NULL, ( SELECT department_id FROM departments WHERE department_code = 'SAFE' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-10-11 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220011', 'baekdohyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '백도현', '010-9003-0023', 'baekdohyun@24hr.example.com', '03186', '서울특별시 종로구 종로 51', '본관 3층', '하나은행', NULL, '백도현', NULL, ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), ( SELECT position_id FROM positions WHERE position_code = 'GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-11-18 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220012', 'namgaeun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '남가은', '010-9004-0024', 'namgaeun@24hr.example.com', '07242', '서울특별시 영등포구 은행로 30', '사무동 4층', '하나은행', NULL, '남가은', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), ( SELECT position_id FROM positions WHERE position_code = 'GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-12-01 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230001', 'simyuchan', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '심유찬', '010-9005-0025', 'simyuchan@24hr.example.com', '16514', '경기도 수원시 영통구 광교로 156', '현장사무소 5층', '하나은행', NULL, '심유찬', NULL, ( SELECT department_id FROM departments WHERE department_code = 'HR' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-01-08 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230002', 'yangseoyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '양서윤', '010-9006-0026', 'yangseoyun@24hr.example.com', '13529', '경기도 성남시 분당구 판교역로 235', '분당오피스 6층', '하나은행', NULL, '양서윤', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIN' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-02-15 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230003', 'nohyunseo', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '노현서', '010-9007-0027', 'nohyunseo@24hr.example.com', '48059', '부산광역시 해운대구 센텀중앙로 90', '부산지사 7층', '하나은행', NULL, '노현서', NULL, ( SELECT department_id FROM departments WHERE department_code = 'PUR' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-03-22 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230004', 'hajunyoung', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '하준영', '010-9008-0028', 'hajunyoung@24hr.example.com', '35209', '대전광역시 서구 둔산대로 100', '대전지사 8층', '하나은행', NULL, '하준영', NULL, ( SELECT department_id FROM departments WHERE department_code = 'DEV' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-04-05 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230005', 'jeondaeun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '전다은', '010-9009-0029', 'jeondaeun@24hr.example.com', '41911', '대구광역시 중구 국채보상로 586', '대구지사 9층', '하나은행', NULL, '전다은', NULL, ( SELECT department_id FROM departments WHERE department_code = 'CONST' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-05-12 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230006', 'yoosiwoo', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '유시우', '010-9000-0030', 'yoosiwoo@24hr.example.com', '61945', '광주광역시 서구 상무중앙로 7', '광주지사 10층', '하나은행', NULL, '유시우', NULL, ( SELECT department_id FROM departments WHERE department_code = 'SAFE' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-06-19 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230007', 'gooyerin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '구예린', '010-9001-0031', 'gooyerin@24hr.example.com', '04524', '서울특별시 중구 세종대로 110', '24HR타워 11층', '하나은행', NULL, '구예린', NULL, ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), ( SELECT position_id FROM positions WHERE position_code = 'DIRECTOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-07-02 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230008', 'minjaehyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '민재현', '010-9002-0032', 'minjaehyun@24hr.example.com', '06236', '서울특별시 강남구 테헤란로 152', '업무동 12층', '하나은행', NULL, '민재현', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), ( SELECT position_id FROM positions WHERE position_code = 'DIRECTOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-08-09 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230009', 'jinseojin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '진서진', '010-9003-0033', 'jinseojin@24hr.example.com', '03186', '서울특별시 종로구 종로 51', '본관 13층', '하나은행', NULL, '진서진', NULL, ( SELECT department_id FROM departments WHERE department_code = 'HR' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-09-16 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230010', 'juarin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '주아린', '010-9004-0034', 'juarin@24hr.example.com', '07242', '서울특별시 영등포구 은행로 30', '사무동 14층', '하나은행', NULL, '주아린', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIN' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-10-23 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230011', 'pyojiho', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '표지호', '010-9005-0035', 'pyojiho@24hr.example.com', '16514', '경기도 수원시 영통구 광교로 156', '현장사무소 15층', '하나은행', NULL, '표지호', NULL, ( SELECT department_id FROM departments WHERE department_code = 'PUR' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-11-06 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230012', 'giseojun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '기서준', '010-9006-0036', 'giseojun@24hr.example.com', '13529', '경기도 성남시 분당구 판교역로 235', '분당오피스 16층', '하나은행', NULL, '기서준', NULL, ( SELECT department_id FROM departments WHERE department_code = 'DEV' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-12-13 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20240001', 'raeunchae', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '라은채', '010-9007-0037', 'raeunchae@24hr.example.com', '48059', '부산광역시 해운대구 센텀중앙로 90', '부산지사 17층', '하나은행', NULL, '라은채', NULL, ( SELECT department_id FROM departments WHERE department_code = 'CONST' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2024-01-20 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20240002', 'madohyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '마도현', '010-9008-0038', 'madohyun@24hr.example.com', '35209', '대전광역시 서구 둔산대로 100', '대전지사 18층', '하나은행', NULL, '마도현', NULL, ( SELECT department_id FROM departments WHERE department_code = 'SAFE' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2024-02-03 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20240003', 'chaseowoo', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '차서우', '010-9009-0039', 'chaseowoo@24hr.example.com', '41911', '대구광역시 중구 국채보상로 586', '대구지사 19층', '하나은행', NULL, '차서우', NULL, ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2024-03-10 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20240004', 'wonjian', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '원지안', '010-9000-0040', 'wonjian@24hr.example.com', '61945', '광주광역시 서구 상무중앙로 7', '광주지사 20층', '하나은행', NULL, '원지안', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2024-04-17 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20240005', 'kimhajun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '김하준', '010-9001-0041', 'kimhajun@24hr.example.com', '04524', '서울특별시 중구 세종대로 110', '24HR타워 1층', '하나은행', NULL, '김하준', NULL, ( SELECT department_id FROM departments WHERE department_code = 'HR' ), ( SELECT position_id FROM positions WHERE position_code = 'GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2024-05-24 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20240006', 'leedohyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '이도현', '010-9002-0042', 'leedohyun@24hr.example.com', '06236', '서울특별시 강남구 테헤란로 152', '업무동 2층', '하나은행', NULL, '이도현', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIN' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2024-06-07 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20240007', 'parkseoa', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '박서아', '010-9003-0043', 'parkseoa@24hr.example.com', '03186', '서울특별시 종로구 종로 51', '본관 3층', '하나은행', NULL, '박서아', NULL, ( SELECT department_id FROM departments WHERE department_code = 'PUR' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2024-07-14 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20240008', 'choijunseo', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '최준서', '010-9004-0044', 'choijunseo@24hr.example.com', '07242', '서울특별시 영등포구 은행로 30', '사무동 4층', '하나은행', NULL, '최준서', NULL, ( SELECT department_id FROM departments WHERE department_code = 'DEV' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2024-08-21 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20240009', 'jungdahyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '정다현', '010-9005-0045', 'jungdahyun@24hr.example.com', '16514', '경기도 수원시 영통구 광교로 156', '현장사무소 5층', '하나은행', NULL, '정다현', NULL, ( SELECT department_id FROM departments WHERE department_code = 'CONST' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2024-09-04 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20240010', 'kangminseok', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '강민석', '010-9006-0046', 'kangminseok@24hr.example.com', '13529', '경기도 성남시 분당구 판교역로 235', '분당오피스 6층', '하나은행', NULL, '강민석', NULL, ( SELECT department_id FROM departments WHERE department_code = 'SAFE' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2024-10-11 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20240011', 'joyejin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '조예진', '010-9007-0047', 'joyejin@24hr.example.com', '48059', '부산광역시 해운대구 센텀중앙로 90', '부산지사 7층', '하나은행', NULL, '조예진', NULL, ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2024-11-18 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20240012', 'yoonseohyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '윤서현', '010-9008-0048', 'yoonseohyun@24hr.example.com', '35209', '대전광역시 서구 둔산대로 100', '대전지사 8층', '하나은행', NULL, '윤서현', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2024-12-01 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20250001', 'jangyuna', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '장유나', '010-9009-0049', 'jangyuna@24hr.example.com', '41911', '대구광역시 중구 국채보상로 586', '대구지사 9층', '하나은행', NULL, '장유나', NULL, ( SELECT department_id FROM departments WHERE department_code = 'HR' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2025-01-08 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20250002', 'imjihwan', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '임지환', '010-9000-0050', 'imjihwan@24hr.example.com', '61945', '광주광역시 서구 상무중앙로 7', '광주지사 10층', '하나은행', NULL, '임지환', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIN' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2025-02-15 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20250003', 'hanseojun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '한서준', '010-9001-0051', 'hanseojun@24hr.example.com', '04524', '서울특별시 중구 세종대로 110', '24HR타워 11층', '하나은행', NULL, '한서준', NULL, ( SELECT department_id FROM departments WHERE department_code = 'PUR' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2025-03-22 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20250004', 'ohjimin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '오지민', '010-9002-0052', 'ohjimin@24hr.example.com', '06236', '서울특별시 강남구 테헤란로 152', '업무동 12층', '하나은행', NULL, '오지민', NULL, ( SELECT department_id FROM departments WHERE department_code = 'DEV' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2025-04-05 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20250005', 'seodoyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '서도윤', '010-9003-0053', 'seodoyun@24hr.example.com', '03186', '서울특별시 종로구 종로 51', '본관 13층', '하나은행', NULL, '서도윤', NULL, ( SELECT department_id FROM departments WHERE department_code = 'CONST' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2025-05-12 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20250006', 'shinhayoung', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '신하영', '010-9004-0054', 'shinhayoung@24hr.example.com', '07242', '서울특별시 영등포구 은행로 30', '사무동 14층', '하나은행', NULL, '신하영', NULL, ( SELECT department_id FROM departments WHERE department_code = 'SAFE' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2025-06-19 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20250007', 'kwonyujin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '권유진', '010-9005-0055', 'kwonyujin@24hr.example.com', '16514', '경기도 수원시 영통구 광교로 156', '현장사무소 15층', '하나은행', NULL, '권유진', NULL, ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), ( SELECT position_id FROM positions WHERE position_code = 'GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2025-07-02 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20250008', 'hwangmingyu', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '황민규', '010-9006-0056', 'hwangmingyu@24hr.example.com', '13529', '경기도 성남시 분당구 판교역로 235', '분당오피스 16층', '하나은행', NULL, '황민규', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), ( SELECT position_id FROM positions WHERE position_code = 'GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2025-08-09 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20250009', 'anseohyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '안서현', '010-9007-0057', 'anseohyun@24hr.example.com', '48059', '부산광역시 해운대구 센텀중앙로 90', '부산지사 17층', '하나은행', NULL, '안서현', NULL, ( SELECT department_id FROM departments WHERE department_code = 'HR' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2025-09-16 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20250010', 'songdain', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '송다인', '010-9008-0058', 'songdain@24hr.example.com', '35209', '대전광역시 서구 둔산대로 100', '대전지사 18층', '하나은행', NULL, '송다인', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIN' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2025-10-23 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20250011', 'ryujunseo', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '류준서', '010-9009-0059', 'ryujunseo@24hr.example.com', '41911', '대구광역시 중구 국채보상로 586', '대구지사 19층', '하나은행', NULL, '류준서', NULL, ( SELECT department_id FROM departments WHERE department_code = 'PUR' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2025-11-06 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20250012', 'hongjia', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '홍지아', '010-9000-0060', 'hongjia@24hr.example.com', '61945', '광주광역시 서구 상무중앙로 7', '광주지사 20층', '하나은행', NULL, '홍지아', NULL, ( SELECT department_id FROM departments WHERE department_code = 'DEV' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2025-12-13 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20260001', 'baeyuchan', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '배유찬', '010-9001-0061', 'baeyuchan@24hr.example.com', '04524', '서울특별시 중구 세종대로 110', '24HR타워 1층', '하나은행', NULL, '배유찬', NULL, ( SELECT department_id FROM departments WHERE department_code = 'CONST' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2026-01-20 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20260002', 'moonseoyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '문서윤', '010-9002-0062', 'moonseoyun@24hr.example.com', '06236', '서울특별시 강남구 테헤란로 152', '업무동 2층', '하나은행', NULL, '문서윤', NULL, ( SELECT department_id FROM departments WHERE department_code = 'SAFE' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2026-02-03 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20260003', 'baeksihyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '백시현', '010-9003-0063', 'baeksihyun@24hr.example.com', '03186', '서울특별시 종로구 종로 51', '본관 3층', '하나은행', NULL, '백시현', NULL, ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), ( SELECT position_id FROM positions WHERE position_code = 'DIRECTOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2026-03-10 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20260004', 'namjunho', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '남준호', '010-9004-0064', 'namjunho@24hr.example.com', '07242', '서울특별시 영등포구 은행로 30', '사무동 4층', '하나은행', NULL, '남준호', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), ( SELECT position_id FROM positions WHERE position_code = 'DIRECTOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2026-04-17 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20260005', 'simgaon', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '심가온', '010-9005-0065', 'simgaon@24hr.example.com', '16514', '경기도 수원시 영통구 광교로 156', '현장사무소 5층', '하나은행', NULL, '심가온', NULL, ( SELECT department_id FROM departments WHERE department_code = 'HR' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2026-05-24 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20260006', 'yangjiyu', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '양지유', '010-9006-0066', 'yangjiyu@24hr.example.com', '13529', '경기도 성남시 분당구 판교역로 235', '분당오피스 6층', '하나은행', NULL, '양지유', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIN' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2026-06-07 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210013', 'nominjae', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '노민재', '010-9007-0067', 'nominjae@24hr.example.com', '48059', '부산광역시 해운대구 센텀중앙로 90', '부산지사 7층', '하나은행', NULL, '노민재', NULL, ( SELECT department_id FROM departments WHERE department_code = 'PUR' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-01-14 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210014', 'haseoyeon', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '하서연', '010-9008-0068', 'haseoyeon@24hr.example.com', '35209', '대전광역시 서구 둔산대로 100', '대전지사 8층', '하나은행', NULL, '하서연', NULL, ( SELECT department_id FROM departments WHERE department_code = 'DEV' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-02-21 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210015', 'jeonharam', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '전하람', '010-9009-0069', 'jeonharam@24hr.example.com', '41911', '대구광역시 중구 국채보상로 586', '대구지사 9층', '하나은행', NULL, '전하람', NULL, ( SELECT department_id FROM departments WHERE department_code = 'CONST' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-03-04 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210016', 'yoodoyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '유도윤', '010-9000-0070', 'yoodoyun@24hr.example.com', '61945', '광주광역시 서구 상무중앙로 7', '광주지사 10층', '하나은행', NULL, '유도윤', NULL, ( SELECT department_id FROM departments WHERE department_code = 'SAFE' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-04-11 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210017', 'gooseojin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '구서진', '010-9001-0071', 'gooseojin@24hr.example.com', '04524', '서울특별시 중구 세종대로 110', '24HR타워 11층', '하나은행', NULL, '구서진', NULL, ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-05-18 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210018', 'minhaeun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '민하은', '010-9002-0072', 'minhaeun@24hr.example.com', '06236', '서울특별시 강남구 테헤란로 152', '업무동 12층', '하나은행', NULL, '민하은', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-06-01 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210019', 'jintaeo', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '진태오', '010-9003-0073', 'jintaeo@24hr.example.com', '03186', '서울특별시 종로구 종로 51', '본관 13층', '하나은행', NULL, '진태오', NULL, ( SELECT department_id FROM departments WHERE department_code = 'HR' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-07-08 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210020', 'juseoa', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '주서아', '010-9004-0074', 'juseoa@24hr.example.com', '07242', '서울특별시 영등포구 은행로 30', '사무동 14층', '하나은행', NULL, '주서아', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIN' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-08-15 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210021', 'pyohyunjoon', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '표현준', '010-9005-0075', 'pyohyunjoon@24hr.example.com', '16514', '경기도 수원시 영통구 광교로 156', '현장사무소 15층', '하나은행', NULL, '표현준', NULL, ( SELECT department_id FROM departments WHERE department_code = 'PUR' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-09-22 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210022', 'giminseo', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '기민서', '010-9006-0076', 'giminseo@24hr.example.com', '13529', '경기도 성남시 분당구 판교역로 235', '분당오피스 16층', '하나은행', NULL, '기민서', NULL, ( SELECT department_id FROM departments WHERE department_code = 'DEV' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-10-05 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210023', 'rasiwoo', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '라시우', '010-9007-0077', 'rasiwoo@24hr.example.com', '48059', '부산광역시 해운대구 센텀중앙로 90', '부산지사 17층', '하나은행', NULL, '라시우', NULL, ( SELECT department_id FROM departments WHERE department_code = 'CONST' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-11-12 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20210024', 'mayujin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '마유진', '010-9008-0078', 'mayujin@24hr.example.com', '35209', '대전광역시 서구 둔산대로 100', '대전지사 18층', '하나은행', NULL, '마유진', NULL, ( SELECT department_id FROM departments WHERE department_code = 'SAFE' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2021-12-19 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220013', 'chadoyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '차도윤', '010-9009-0079', 'chadoyun@24hr.example.com', '41911', '대구광역시 중구 국채보상로 586', '대구지사 19층', '하나은행', NULL, '차도윤', NULL, ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-01-02 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220014', 'wonseojun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '원서준', '010-9000-0080', 'wonseojun@24hr.example.com', '61945', '광주광역시 서구 상무중앙로 7', '광주지사 20층', '하나은행', NULL, '원서준', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-02-09 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220015', 'kimyejun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '김예준', '010-9001-0081', 'kimyejun@24hr.example.com', '04524', '서울특별시 중구 세종대로 110', '24HR타워 1층', '하나은행', NULL, '김예준', NULL, ( SELECT department_id FROM departments WHERE department_code = 'HR' ), ( SELECT position_id FROM positions WHERE position_code = 'DEPUTY_GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-03-16 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220016', 'leeharin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '이하린', '010-9002-0082', 'leeharin@24hr.example.com', '06236', '서울특별시 강남구 테헤란로 152', '업무동 2층', '하나은행', NULL, '이하린', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIN' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-04-23 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220017', 'parkminseo', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '박민서', '010-9003-0083', 'parkminseo@24hr.example.com', '03186', '서울특별시 종로구 종로 51', '본관 3층', '하나은행', NULL, '박민서', NULL, ( SELECT department_id FROM departments WHERE department_code = 'PUR' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-05-06 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220018', 'choidohyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '최도현', '010-9004-0084', 'choidohyun@24hr.example.com', '07242', '서울특별시 영등포구 은행로 30', '사무동 4층', '하나은행', NULL, '최도현', NULL, ( SELECT department_id FROM departments WHERE department_code = 'DEV' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-06-13 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220019', 'jungyuna', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '정유나', '010-9005-0085', 'jungyuna@24hr.example.com', '16514', '경기도 수원시 영통구 광교로 156', '현장사무소 5층', '하나은행', NULL, '정유나', NULL, ( SELECT department_id FROM departments WHERE department_code = 'CONST' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-07-20 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220020', 'kangseojun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '강서준', '010-9006-0086', 'kangseojun@24hr.example.com', '13529', '경기도 성남시 분당구 판교역로 235', '분당오피스 6층', '하나은행', NULL, '강서준', NULL, ( SELECT department_id FROM departments WHERE department_code = 'SAFE' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-08-03 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220021', 'johaeun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '조하은', '010-9007-0087', 'johaeun@24hr.example.com', '48059', '부산광역시 해운대구 센텀중앙로 90', '부산지사 7층', '하나은행', NULL, '조하은', NULL, ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), ( SELECT position_id FROM positions WHERE position_code = 'GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-09-10 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220022', 'yoonmingyu', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '윤민규', '010-9008-0088', 'yoonmingyu@24hr.example.com', '35209', '대전광역시 서구 둔산대로 100', '대전지사 8층', '하나은행', NULL, '윤민규', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), ( SELECT position_id FROM positions WHERE position_code = 'GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-10-17 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220023', 'jangharin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '장하린', '010-9009-0089', 'jangharin@24hr.example.com', '41911', '대구광역시 중구 국채보상로 586', '대구지사 9층', '하나은행', NULL, '장하린', NULL, ( SELECT department_id FROM departments WHERE department_code = 'HR' ), ( SELECT position_id FROM positions WHERE position_code = 'GENERAL_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-11-24 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20220024', 'imseoyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '임서윤', '010-9000-0090', 'imseoyun@24hr.example.com', '61945', '광주광역시 서구 상무중앙로 7', '광주지사 10층', '하나은행', NULL, '임서윤', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIN' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2022-12-07 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230013', 'handoyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '한도윤', '010-9001-0091', 'handoyun@24hr.example.com', '04524', '서울특별시 중구 세종대로 110', '24HR타워 11층', '하나은행', NULL, '한도윤', NULL, ( SELECT department_id FROM departments WHERE department_code = 'PUR' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-01-14 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230014', 'ohseojun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '오서준', '010-9002-0092', 'ohseojun@24hr.example.com', '06236', '서울특별시 강남구 테헤란로 152', '업무동 12층', '하나은행', NULL, '오서준', NULL, ( SELECT department_id FROM departments WHERE department_code = 'DEV' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-02-21 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230015', 'seohaneul', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '서하늘', '010-9003-0093', 'seohaneul@24hr.example.com', '03186', '서울특별시 종로구 종로 51', '본관 13층', '하나은행', NULL, '서하늘', NULL, ( SELECT department_id FROM departments WHERE department_code = 'CONST' ), ( SELECT position_id FROM positions WHERE position_code = 'SENIOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-03-04 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230016', 'shinminjun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '신민준', '010-9004-0094', 'shinminjun@24hr.example.com', '07242', '서울특별시 영등포구 은행로 30', '사무동 14층', '하나은행', NULL, '신민준', NULL, ( SELECT department_id FROM departments WHERE department_code = 'SAFE' ), ( SELECT position_id FROM positions WHERE position_code = 'MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-04-11 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230017', 'kwonseoa', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '권서아', '010-9005-0095', 'kwonseoa@24hr.example.com', '16514', '경기도 수원시 영통구 광교로 156', '현장사무소 15층', '하나은행', NULL, '권서아', NULL, ( SELECT department_id FROM departments WHERE department_code = 'MGMT' ), ( SELECT position_id FROM positions WHERE position_code = 'DIRECTOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-05-18 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230018', 'hwangdohyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '황도현', '010-9006-0096', 'hwangdohyun@24hr.example.com', '13529', '경기도 성남시 분당구 판교역로 235', '분당오피스 16층', '하나은행', NULL, '황도현', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIELD' ), ( SELECT position_id FROM positions WHERE position_code = 'DIRECTOR' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-06-01 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230019', 'anyujin', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '안유진', '010-9007-0097', 'anyujin@24hr.example.com', '48059', '부산광역시 해운대구 센텀중앙로 90', '부산지사 17층', '하나은행', NULL, '안유진', NULL, ( SELECT department_id FROM departments WHERE department_code = 'HR' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-07-08 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230020', 'songminjae', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '송민재', '010-9008-0098', 'songminjae@24hr.example.com', '35209', '대전광역시 서구 둔산대로 100', '대전지사 18층', '하나은행', NULL, '송민재', NULL, ( SELECT department_id FROM departments WHERE department_code = 'FIN' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-08-15 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230021', 'ryuseoyeon', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '류서연', '010-9009-0099', 'ryuseoyeon@24hr.example.com', '41911', '대구광역시 중구 국채보상로 586', '대구지사 19층', '하나은행', NULL, '류서연', NULL, ( SELECT department_id FROM departments WHERE department_code = 'PUR' ), ( SELECT position_id FROM positions WHERE position_code = 'STAFF' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-09-22 09:00:00', NULL );

INSERT INTO users ( employee_id, employee_no, login_id, password, name, phone, email, zipcode, address, address_detail, bank_name, account_number, account_holder, rrn, department_id, position_id, employment_type, status, is_first_login, last_login_at, hire_date, resignation_date )
VALUES ( employee_seq.NEXTVAL, 'EMP20230022', 'hongdoyun', '$2y$10$qQTjZ.aOLoBavK45/.gNresi4PRWjsyEpec7TBj4/kiV8Uil8WkfC', '홍도윤', '010-9000-0100', 'hongdoyun@24hr.example.com', '61945', '광주광역시 서구 상무중앙로 7', '광주지사 20층', '하나은행', NULL, '홍도윤', NULL, ( SELECT department_id FROM departments WHERE department_code = 'DEV' ), ( SELECT position_id FROM positions WHERE position_code = 'ASSISTANT_MANAGER' ), 'REGULAR', 'ACTIVE', 'N', NULL, TIMESTAMP '2023-10-05 09:00:00', NULL );


-- 5. 사용자 역할 매핑 테이블 샘플 데이터

INSERT INTO user_roles ( user_roles_id, employee_id, role_id )
SELECT user_role_seq.NEXTVAL, u.employee_id, r.role_id
FROM users u
JOIN roles r ON r.role_code = 'USER'
WHERE NOT EXISTS (
    SELECT 1
    FROM user_roles ur
    WHERE ur.employee_id = u.employee_id
      AND ur.role_id = r.role_id
);

-- 직원별 연차 잔액 샘플 데이터
INSERT INTO annual_leave_balances (
    annual_leave_balance_id,
    employee_id,
    leave_year,
    total_days,
    remaining_days,
    granted_at,
    expires_at
)
SELECT
    annual_leave_balance_seq.NEXTVAL,
    u.employee_id,
    EXTRACT(YEAR FROM SYSDATE),
    15.00,
    15.00,
    SYSTIMESTAMP,
    ADD_MONTHS(SYSTIMESTAMP, 12)
FROM users u
WHERE u.employment_type = 'REGULAR'
  AND u.status = 'ACTIVE'
  AND NOT EXISTS (
      SELECT 1
      FROM annual_leave_balances alb
      WHERE alb.employee_id = u.employee_id
        AND alb.leave_year = EXTRACT(YEAR FROM SYSDATE)
  );

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
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver, department_id) VALUES (approval_line_seq.NEXTVAL, 1, 1, 2, 2); 
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver, department_id) VALUES (approval_line_seq.NEXTVAL, 2, 1, 2, 2);
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver, department_id) VALUES (approval_line_seq.NEXTVAL, 3, 1, 2, 2);
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver, department_id) VALUES (approval_line_seq.NEXTVAL, 4, 1, 2, 2); -- 지출결의 1차: 인사팀장
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver, department_id) VALUES (approval_line_seq.NEXTVAL, 4, 2, 1, NULL); -- 지출결의 2차: 대표이사
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver, department_id) VALUES (approval_line_seq.NEXTVAL, 5, 1, 6, NULL); -- 구매요청 1차: 구매팀장
INSERT INTO approval_line (approval_line_id, document_type, step_order, default_approver, department_id) VALUES (approval_line_seq.NEXTVAL, 5, 2, 1, NULL); -- 구매요청 2차: 대표이사


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
