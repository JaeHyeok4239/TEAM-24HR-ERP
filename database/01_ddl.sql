-- 1. 부서 테이블
CREATE TABLE departments (
    department_id    NUMBER          NOT NULL,
    department_code  VARCHAR2(50)    NOT NULL,
    department_name  VARCHAR2(100)   NOT NULL,
    description      VARCHAR2(255),
    is_active        CHAR(1)         DEFAULT 'Y' NOT NULL,
    created_at       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP,

    CONSTRAINT pk_departments PRIMARY KEY (department_id),
    CONSTRAINT uk_departments_code UNIQUE (department_code),
    CONSTRAINT chk_departments_active CHECK (is_active IN ('Y', 'N'))
);

-- 2. 직급 테이블
CREATE TABLE positions (
    position_id    NUMBER          NOT NULL,
    position_code  VARCHAR2(50)    NOT NULL,
    position_name  VARCHAR2(100)   NOT NULL,
    description    VARCHAR2(255),
    sort_order     NUMBER          NOT NULL,
    is_active      CHAR(1)         DEFAULT 'Y' NOT NULL,
    created_at     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP,

    CONSTRAINT pk_positions PRIMARY KEY (position_id),
    CONSTRAINT uk_positions_code UNIQUE (position_code),
    CONSTRAINT chk_positions_active CHECK (is_active IN ('Y', 'N'))
);

-- 3. 역할 테이블
CREATE TABLE roles (
    role_id      NUMBER          NOT NULL,
    role_code    VARCHAR2(50)    NOT NULL,
    role_name    VARCHAR2(100)   NOT NULL,
    description  VARCHAR2(255),
    is_active    CHAR(1)         DEFAULT 'Y' NOT NULL,
    created_at   TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP,

    CONSTRAINT pk_roles PRIMARY KEY (role_id),
    CONSTRAINT uk_roles_code UNIQUE (role_code),
    CONSTRAINT chk_roles_active CHECK (is_active IN ('Y', 'N'))
);

-- 4. 사용자 테이블
CREATE TABLE users (
    employee_id       NUMBER          NOT NULL,
    employee_no       VARCHAR2(30)    NOT NULL,
    login_id          VARCHAR2(50)    NOT NULL,
    password          VARCHAR2(255)   NOT NULL,
    name              VARCHAR2(50)    NOT NULL,
    phone             VARCHAR2(20),
    email             VARCHAR2(100),
    zipcode           VARCHAR2(10),
    address           VARCHAR2(255),
    address_detail    VARCHAR2(255),
    bank_name         VARCHAR2(50),
    account_number    VARCHAR2(50),
    account_holder    VARCHAR2(50),
    rrn               VARCHAR2(255),
    department_id     NUMBER,
    position_id       NUMBER,
    employment_type   VARCHAR2(20)    DEFAULT 'REGULAR' NOT NULL,
    status            VARCHAR2(20)    DEFAULT 'ACTIVE' NOT NULL,
    is_first_login    CHAR(1)         DEFAULT 'Y' NOT NULL,
    last_login_at     TIMESTAMP,
    hire_date         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    resignation_date  TIMESTAMP,
    created_at        TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP,

    CONSTRAINT pk_users PRIMARY KEY (employee_id),
    CONSTRAINT uk_users_employee_no UNIQUE (employee_no),
    CONSTRAINT uk_users_login_id UNIQUE (login_id),
    CONSTRAINT fk_users_department FOREIGN KEY (department_id)
        REFERENCES departments(department_id),
    CONSTRAINT fk_users_position FOREIGN KEY (position_id)
        REFERENCES positions(position_id),
    CONSTRAINT chk_users_employment_type CHECK (employment_type IN ('REGULAR', 'DAILY')),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'LOCKED', 'INACTIVE', 'LEAVE', 'RESIGNED')),
    CONSTRAINT chk_users_first_login CHECK (is_first_login IN ('Y', 'N'))
);

-- 5. 사용자 역할 매핑 테이블
CREATE TABLE user_roles (
    user_roles_id  NUMBER  NOT NULL,
    employee_id    NUMBER  NOT NULL,
    role_id        NUMBER  NOT NULL,

    CONSTRAINT pk_user_roles PRIMARY KEY (user_roles_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (employee_id)
        REFERENCES users(employee_id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id)
        REFERENCES roles(role_id),
    CONSTRAINT uk_user_roles_user_role UNIQUE (employee_id, role_id)
);


-- 근태관리: 근무 시간 규칙
CREATE TABLE attendance_time_policies(
   attendance_time_policy_id NUMBER NOT NULL,
   employment_type  VARCHAR2(20)    NOT NULL,
   policy_type      VARCHAR2(20)    NOT NULL,
   day_of_week      VARCHAR2(10)    NOT NULL,
   start_time       NUMBER(4)       NOT NULL,
   end_time         NUMBER(4)       NOT NULL,
   created_at       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
   updated_at       TIMESTAMP       NULL,
   
   CONSTRAINT pk_attendance_time_policies PRIMARY KEY (attendance_time_policy_id),
   
   CONSTRAINT chk_att_time_start_range CHECK (start_time BETWEEN 0 AND 2359),
   CONSTRAINT chk_att_time_end_range CHECK (end_time BETWEEN 0 AND 2359),

   CONSTRAINT chk_att_time_start_minute CHECK (MOD (start_time, 100) < 60),
   CONSTRAINT chk_att_time_end_minute CHECK (MOD (end_time, 100) < 60),
   
   CONSTRAINT chk_att_time_emp_type CHECK (employment_type IN ('REGULAR', 'DAILY'))
);
-- 근태관리: 근태 판정 기준
CREATE TABLE attendance_thresholds(
   attendance_threshold_id  NUMBER          NOT NULL,
   employment_type          VARCHAR2(20)    NOT NULL,
   threshold_type           VARCHAR2(20)    NOT NULL,
   threshold_minutes        NUMBER          NOT NULL,
   threshold_description    VARCHAR2(255)   NULL,
   created_at               TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
   updated_at               TIMESTAMP       NULL,

   CONSTRAINT pk_attendance_thresholds PRIMARY KEY (attendance_threshold_id),   
   CONSTRAINT chk_att_thresholds_emp_type CHECK (employment_type IN ('REGULAR', 'DAILY'))
);
-- 근태관리: 근무지
CREATE TABLE workplaces (
   workplace_id     NUMBER          NOT NULL,
   workplace_code   VARCHAR2(30)    NOT NULL,
   workplace_name   VARCHAR2(100)   NOT NULL,
   workplace_address VARCHAR2(255)  NULL,
   radius_meter     NUMBER          NOT NULL,
   latitude         NUMBER(10,7)    NOT NULL,
   longitude        NUMBER(10,7)    NOT NULL,
   created_at       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
   updated_at       TIMESTAMP       NULL,

   CONSTRAINT pk_workplaces PRIMARY KEY (workplace_id)
);
-- 근태관리: 출퇴근 기록
CREATE TABLE attendance_logs(
   attendance_log_id    NUMBER          NOT NULL,
   employee_id          VARCHAR2(30)    NOT NULL,
   log_type             VARCHAR2(10)    NOT NULL,
   log_time             TIMESTAMP       NOT NULL,
   latitude             NUMBER(10,7)    NOT NULL,
   longitude            NUMBER(10,7)    NOT NULL,
   is_location_valid    CHAR(1)         DEFAULT 'N' NOT NULL,
   workplace_id         NUMBER          NULL,
   memo                 VARCHAR2(255),
   created_at           TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
   updated_at           TIMESTAMP       NULL,

   CONSTRAINT pk_attendance_logs PRIMARY KEY (attendance_log_id),
   CONSTRAINT fk_att_logs_employee FOREIGN KEY (employee_id) REFERENCES users(employee_id),
   CONSTRAINT fk_att_logs_workplace FOREIGN KEY (workplace_id) REFERENCES workplaces(workplace_id)
);
-- 근태관리: 근태 결과
CREATE TABLE attendance_results(
   attendance_result_id     NUMBER          NOT NULL,
   
   attendance_status_id     NUMBER          NOT NULL,
   attendance_threshold_id  NUMBER          NOT NULL,
   approval_status_id       NUMBER          NOT NULL,
   half_day_type_id         NUMBER          NULL,
   holiday_id               NUMBER          NULL,
   correction_type_id      NUMBER          NOT NULL,
   correction_reason_type_id    NUMBER      NOT NULL,
   employee_id              NUMBER          NOT NULL,

   work_date                DATE            NOT NULL,
   check_in_time            TIMESTAMP       NULL,
   check_out_time           TIMESTAMP       NULL,
   total_work_minutes       NUMBER          NULL,
   actual_work_minutes      NUMBER          NULL,
   overtime_minutes         NUMBER          NULL,
   is_holiday_work          CHAR(1)         DEFAULT 'N' NOT NULL,
   is_missing_checkout      CHAR(1)         DEFAULT 'N' NOT NULL,
   is_correction_required   CHAR(1)         DEFAULT 'N' NOT NULL,
   processing_status        VARCHAR2(30)    DEFAULT 'NORMAL' NOT NULL,
   correction_reason        VARCHAR2(255)   NULL,

   created_at               TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
   updated_at               TIMESTAMP       NULL,
   
   CONSTRAINT pk_att_attendance_results PRIMARY KEY (attendance_result_id),
   
   CONSTRAINT fk_att_attendance_status_id FOREIGN KEY (attendance_status_id) REFERENCES attendance_statuses(attendance_status_id),
   CONSTRAINT fk_att_attendance_threshold_id FOREIGN KEY (attendance_threshold_id) REFERENCES attendance_thresholds(attendance_threshold_id),
   CONSTRAINT fk_att_employee_id FOREIGN KEY (employee_id) REFERENCES users(employee_id),
   CONSTRAINT fk_att_half_day_type_id FOREIGN KEY (half_day_type_id) REFERENCES half_day_types(half_day_type_id),
   CONSTRAINT fk_att_correction_type_id FOREIGN KEY (correction_type_id) REFERENCES correction_types(correction_type_id),
   CONSTRAINT fk_att_correction_reason_type_id FOREIGN KEY (correction_reason_type_id) REFERENCES correction_reason_types(correction_reason_type_id),
   CONSTRAINT fk_att_approval_status_id FOREIGN KEY (approval_status_id) REFERENCES approval_statuses(approval_status_id),
   CONSTRAINT fk_att_holiday_id FOREIGN KEY (holiday_id) REFERENCES holidays(holiday_id),

   CONSTRAINT uq_att_attendance_results UNIQUE (employee_id, work_date)
);


-- 근태관리: 정정 종류(출근 정정/퇴근 정정/근태 상태 정정)
CREATE TABLE correction_types(
   correction_type_id   NUMBER          NOT NULL,
   type_code            VARCHAR2(10)    NOT NULL,
   type_name            VARCHAR2(30)    NOT NULL,
   CONSTRAINT pk_correction_types PRIMARY KEY (correction_type_id)
);
-- 근태관리: 정정 사유 구분(단순 입력 오류/증빙 지연 제출/기타 사유)
CREATE TABLE correction_reason_types(
   correction_reason_type_id    NUMBER          NOT NULL,
   reason_code                  VARCHAR2(30)    NOT NULL,
   reason_name                  VARCHAR2(50)    NOT NULL,
   CONSTRAINT pk_correction_reason_types PRIMARY KEY (correction_reason_type_id)
);
-- 근태관리: 결재 상태(승인 대기/승인 완료/반려)
CREATE TABLE approval_statuses(
   approval_status_id   NUMBER          NOT NULL,
   status_code          VARCHAR2(30)    NOT NULL,
   status_name          VARCHAR2(30)    NOT NULL,
   CONSTRAINT pk_approval_statuses PRIMARY KEY (approval_status_id)
);
-- 근태관리: 근태 상태(근무/지각/조퇴/결근/휴가/미퇴근)
CREATE TABLE attendance_statuses(
   attendance_status_id NUMBER          NOT NULL,
   status_code          VARCHAR2(30)    NOT NULL,
   status_name          VARCHAR2(50)    NOT NULL,
   status_priority      NUMBER          NOT NULL,
   CONSTRAINT pk_attendance_statues PRIMARY KEY (attendance_status_id)
);
-- 근태관리: 반차 종류(연차/반차)
CREATE TABLE half_day_types(
   half_day_type_id NUMBER          NOT NULL,
   type_code        VARCHAR2(10)    NOT NULL,
   type_name        VARCHAR2(30)    NOT NULL,
   CONSTRAINT pk_half_day_types PRIMARY KEY (half_day_type_id)
);
