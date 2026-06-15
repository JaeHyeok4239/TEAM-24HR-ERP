-- 1. 부서 테이블
CREATE TABLE
    departments (
        department_id NUMBER NOT NULL,
        department_code VARCHAR2 (50) NOT NULL,
        department_name VARCHAR2 (100) NOT NULL,
        parent_department_id NUMBER,
        description VARCHAR2 (255),
        is_active CHAR(1) DEFAULT 'Y' NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        updated_at TIMESTAMP,
        CONSTRAINT pk_departments PRIMARY KEY (department_id),
        CONSTRAINT uk_departments_code UNIQUE (department_code),
        CONSTRAINT fk_departments_parent FOREIGN KEY (parent_department_id) REFERENCES departments (department_id),
        CONSTRAINT chk_departments_active CHECK (is_active IN ('Y', 'N'))
    );

-- 2. 직급 테이블
CREATE TABLE
    positions (
        position_id NUMBER NOT NULL,
        position_code VARCHAR2 (50) NOT NULL,
        position_name VARCHAR2 (100) NOT NULL,
        description VARCHAR2 (255),
        sort_order NUMBER NOT NULL,
        is_active CHAR(1) DEFAULT 'Y' NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        updated_at TIMESTAMP,
        CONSTRAINT pk_positions PRIMARY KEY (position_id),
        CONSTRAINT uk_positions_code UNIQUE (position_code),
        CONSTRAINT chk_positions_active CHECK (is_active IN ('Y', 'N'))
    );

-- 3. 역할 테이블
CREATE TABLE
    roles (
        role_id NUMBER NOT NULL,
        role_code VARCHAR2 (50) NOT NULL,
        role_name VARCHAR2 (100) NOT NULL,
        description VARCHAR2 (255),
        is_active CHAR(1) DEFAULT 'Y' NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        updated_at TIMESTAMP,
        CONSTRAINT pk_roles PRIMARY KEY (role_id),
        CONSTRAINT uk_roles_code UNIQUE (role_code),
        CONSTRAINT chk_roles_active CHECK (is_active IN ('Y', 'N'))
    );

-- 4. 사용자 테이블
CREATE TABLE
    users (
        employee_id NUMBER NOT NULL,
        employee_no VARCHAR2 (30) NOT NULL,
        login_id VARCHAR2 (50) NOT NULL,
        password VARCHAR2 (255) NOT NULL,
        name VARCHAR2 (50) NOT NULL,
        phone VARCHAR2 (20),
        email VARCHAR2 (100),
        zipcode VARCHAR2 (10),
        address VARCHAR2 (255),
        address_detail VARCHAR2 (255),
        bank_name VARCHAR2 (50),
        account_number VARCHAR2 (50),
        account_holder VARCHAR2 (50),
        rrn VARCHAR2 (255),
        department_id NUMBER,
        position_id NUMBER,
        employment_type VARCHAR2 (20) DEFAULT 'REGULAR' NOT NULL,
        status VARCHAR2 (20) DEFAULT 'ACTIVE' NOT NULL,
        is_first_login CHAR(1) DEFAULT 'Y' NOT NULL,
        last_login_at TIMESTAMP,
        hire_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        resignation_date TIMESTAMP,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        updated_at TIMESTAMP,
        CONSTRAINT pk_users PRIMARY KEY (employee_id),
        CONSTRAINT uk_users_employee_no UNIQUE (employee_no),
        CONSTRAINT uk_users_login_id UNIQUE (login_id),
        CONSTRAINT fk_users_department FOREIGN KEY (department_id) REFERENCES departments (department_id),
        CONSTRAINT fk_users_position FOREIGN KEY (position_id) REFERENCES positions (position_id),
        CONSTRAINT chk_users_employment_type CHECK (employment_type IN ('REGULAR', 'DAILY')),
        CONSTRAINT chk_users_status CHECK (
            status IN (
                'ACTIVE',
                'LOCKED',
                'INACTIVE',
                'LEAVE',
                'RESIGNED'
            )
        ),
        CONSTRAINT chk_users_first_login CHECK (is_first_login IN ('Y', 'N'))
    );

-- 5. 사용자 역할 매핑 테이블
CREATE TABLE
    user_roles (
        user_roles_id NUMBER NOT NULL,
        employee_id NUMBER NOT NULL,
        role_id NUMBER NOT NULL,
        CONSTRAINT pk_user_roles PRIMARY KEY (user_roles_id),
        CONSTRAINT fk_user_roles_user FOREIGN KEY (employee_id) REFERENCES users (employee_id),
        CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (role_id),
        CONSTRAINT uk_user_roles_user_role UNIQUE (employee_id, role_id)
    );





-- 근태관리: 근무 시간 규칙
CREATE TABLE
    attendance_time_policies (
        attendance_time_policy_id NUMBER NOT NULL,
        employment_type VARCHAR2 (20) NOT NULL, -- 직원 구분(REGULAR/DAILY)
        policy_type VARCHAR2 (20) NOT NULL, -- 규칙 타입(WORK/BREAK/HALF_AM/HALF_PM)
        day_of_week VARCHAR2 (10) NOT NULL, -- 요일 코드(MON~SUN)
        start_time NUMBER (4) NOT NULL, -- 규칙 시작 시간(HHMM)
        end_time NUMBER (4) NOT NULL, -- 규칙 종료 시간(HHMM) ex. 오전 9시는 0900
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        updated_at TIMESTAMP NULL,
        CONSTRAINT pk_attendance_time_policies PRIMARY KEY (attendance_time_policy_id),
        CONSTRAINT chk_att_time_start_range CHECK (start_time BETWEEN 0 AND 2359),
        CONSTRAINT chk_att_time_end_range CHECK (end_time BETWEEN 0 AND 2359),
        CONSTRAINT chk_att_time_start_minute CHECK (MOD(start_time, 100) < 60),
        CONSTRAINT chk_att_time_end_minute CHECK (MOD(end_time, 100) < 60),
        CONSTRAINT chk_att_time_emp_type CHECK (employment_type IN ('REGULAR', 'DAILY')),
        CONSTRAINT chk_att_time_day_week CHECK (day_of_week IN ('MON','TUE','WED','THU','FRI','SAT','SUN'))
    );

-- 근태관리: 근태 판정 기준
CREATE TABLE
    attendance_thresholds (
        attendance_threshold_id NUMBER NOT NULL,
        employment_type VARCHAR2 (20) NOT NULL, -- 직원 구분(REGULAR/DAILY)
        threshold_type VARCHAR2 (20) NOT NULL, -- 기준 타입(LATE/EARLY_LEAVE/ABSENCE)
        threshold_minutes NUMBER NOT NULL, -- 판정 기준 시간(분 단위)
        threshold_description VARCHAR2 (255) NULL, -- 기준 설명
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        updated_at TIMESTAMP NULL,
        CONSTRAINT pk_attendance_thresholds PRIMARY KEY (attendance_threshold_id),
        CONSTRAINT chk_att_thresholds_emp_type CHECK (employment_type IN ('REGULAR', 'DAILY'))
    );

-- 근태관리: 근무지
CREATE TABLE
    workplaces (
        workplace_id NUMBER NOT NULL,
        workplace_code VARCHAR2 (30) NOT NULL,
        workplace_name VARCHAR2 (100) NOT NULL,
        workplace_address VARCHAR2 (255) NULL,
        radius_meter NUMBER NOT NULL, -- 출퇴근 허용 반경(m)
        latitude NUMBER (10, 7) NOT NULL, -- 근무지 위도
        longitude NUMBER (10, 7) NOT NULL, -- 근무지 경도
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        updated_at TIMESTAMP NULL,
        CONSTRAINT pk_workplaces PRIMARY KEY (workplace_id)
    );

-- 근태관리: 출퇴근 기록
CREATE TABLE
    attendance_logs (
        attendance_log_id NUMBER NOT NULL,
        employee_id NUMBER NOT NULL,
        log_type VARCHAR2 (10) NOT NULL, -- 출퇴근 구분(IN/OUT)
        log_time TIMESTAMP NOT NULL, -- 출퇴근 기록 시간
        latitude NUMBER (10, 7) NOT NULL, -- GPS 위도
        longitude NUMBER (10, 7) NOT NULL, -- GPS 경도
        is_location_valid CHAR(1) DEFAULT 'N' NOT NULL, -- 위치 인증 여부(Y/N)
        workplace_id NUMBER NULL, -- 근무지
        work_date DATE NOT NULL, -- 근무 날짜
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        updated_at TIMESTAMP NULL,
        CONSTRAINT pk_attendance_logs PRIMARY KEY (attendance_log_id),
        CONSTRAINT fk_att_logs_employee FOREIGN KEY (employee_id) REFERENCES users (employee_id),
        CONSTRAINT fk_att_logs_workplace FOREIGN KEY (workplace_id) REFERENCES workplaces (workplace_id),
        CONSTRAINT uk_att_logs UNIQUE (employee_id, work_date, log_type)
    );

-- 근태관리: 근태 결과
CREATE TABLE
    attendance_results (
        attendance_result_id NUMBER NOT NULL,
        attendance_status_id NUMBER NOT NULL,
        attendance_threshold_id NUMBER NOT NULL,
        approval_status_id NUMBER NOT NULL,
        half_day_type_id NUMBER NULL,
        holiday_id NUMBER NULL,
        correction_type_id NUMBER NULL,
        correction_reason_type_id NUMBER NULL, 
        employee_id NUMBER NOT NULL, 
        work_date DATE NOT NULL, -- 근무 기준 날짜
        check_in_time TIMESTAMP NULL, -- 출근 시간
        check_out_time TIMESTAMP NULL, -- 퇴근 시간
        total_work_minutes NUMBER NULL, -- 총 근무 시간(분)
        actual_work_minutes NUMBER NULL, -- 휴게 제외 실 근무 시간(분)
        overtime_minutes NUMBER NULL, -- 초과 근무 시간(분)
        is_holiday_work CHAR(1) DEFAULT 'N' NOT NULL, -- 휴일 근무 여부(Y/N)
        is_missing_checkout CHAR(1) DEFAULT 'N' NOT NULL, -- 미퇴근 여부(Y/N)
        is_correction_required CHAR(1) DEFAULT 'N' NOT NULL, -- 정정 필요 여부(Y/N)
        processing_status VARCHAR2 (30) DEFAULT 'NORMAL' NOT NULL, -- 처리 상태(NORMAL/CORRECTION_REQUIRED/CORRECTED)
        correction_reason VARCHAR2 (255) NULL, -- 근태 정정 사유
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        updated_at TIMESTAMP NULL,
        CONSTRAINT pk_att_attendance_results PRIMARY KEY (attendance_result_id),
        CONSTRAINT fk_att_attendance_status_id FOREIGN KEY (attendance_status_id) REFERENCES attendance_statuses (attendance_status_id),
        CONSTRAINT fk_att_attendance_threshold_id FOREIGN KEY (attendance_threshold_id) REFERENCES attendance_thresholds (attendance_threshold_id),
        CONSTRAINT fk_att_employee_id FOREIGN KEY (employee_id) REFERENCES users (employee_id),
        CONSTRAINT fk_att_half_day_type_id FOREIGN KEY (half_day_type_id) REFERENCES half_day_types (half_day_type_id),
        CONSTRAINT fk_att_correction_type_id FOREIGN KEY (correction_type_id) REFERENCES correction_types (correction_type_id),
        CONSTRAINT fk_att_correction_reason_type_id FOREIGN KEY (correction_reason_type_id) REFERENCES correction_reason_types (correction_reason_type_id),
        CONSTRAINT fk_att_approval_status_id FOREIGN KEY (approval_status_id) REFERENCES approval_statuses (approval_status_id),
        CONSTRAINT fk_att_holiday_id FOREIGN KEY (holiday_id) REFERENCES holidays (holiday_id),
        CONSTRAINT uq_att_attendance_results UNIQUE (employee_id, work_date)
    );

-- 근태관리: 정정 종류(출근 정정/퇴근 정정/근태 상태 정정)
CREATE TABLE
    correction_types (
        correction_type_id NUMBER NOT NULL,
        type_code VARCHAR2 (10) NOT NULL, -- 타입 코드(IN/OUT/STATUS)
        type_name VARCHAR2 (30) NOT NULL, -- 타입 이름(출근 정정/퇴근 정정/근태 상태 정정)
        CONSTRAINT pk_correction_types PRIMARY KEY (correction_type_id)
    );

-- 근태관리: 정정 사유 구분(단순 입력 오류/증빙 지연 제출/기타 사유)
CREATE TABLE
    correction_reason_types (
        correction_reason_type_id NUMBER NOT NULL,
        reason_code VARCHAR2 (30) NOT NULL, -- 사유 코드(SIMPLE/DELAY_DOCUMENT/ETC)
        reason_name VARCHAR2 (50) NOT NULL, -- 사유 이름(단순 입력 오류/증빙 지연 제출/기타 사유)
        CONSTRAINT pk_correction_reason_types PRIMARY KEY (correction_reason_type_id)
    );

-- 근태관리: 결재 상태(승인 대기/승인 완료/반려)
CREATE TABLE
    approval_statuses (
        approval_status_id NUMBER NOT NULL,
        status_code VARCHAR2 (30) NOT NULL, -- 상태 코드(PENDING/APPROVED/REJECTED)
        status_name VARCHAR2 (30) NOT NULL, -- 상태 이름(승인 대기/승인 완료/반려)
        CONSTRAINT pk_approval_statuses PRIMARY KEY (approval_status_id)
    );

-- 근태관리: 근태 상태(근무/지각/조퇴/결근/휴가/미퇴근)
CREATE TABLE
    attendance_statuses (
        attendance_status_id NUMBER NOT NULL,
        status_code VARCHAR2 (30) NOT NULL, -- 상태 코드(WORK/LATE/EARLY_LEAVE/ABSENT/LEAVE/MISSING_CHECKOUT)
        status_name VARCHAR2 (50) NOT NULL, -- 상태 이름(근무/지각/조퇴/결근/휴가/미퇴근)
        status_priority NUMBER NOT NULL, -- 상태 판정 우선순위(휴가(연차>반차)>결근>반차>조퇴>지각>근무)
        CONSTRAINT pk_attendance_statuses PRIMARY KEY (attendance_status_id)
    );

-- 근태관리: 반차 종류(연차/반차)
CREATE TABLE
    half_day_types (
        half_day_type_id NUMBER NOT NULL,
        type_code VARCHAR2 (10) NOT NULL, -- 타입 코드(NONE/AM/PM)
        type_name VARCHAR2 (30) NOT NULL, -- 타입 이름(미사용/오전 반차/오후 반차)
        CONSTRAINT pk_half_day_types PRIMARY KEY (half_day_type_id)
    );





-- ------------결재 -----------------

-- 문서 유형 (연차신청서, 지출결의서 등 결재 문서의 종류)
CREATE TABLE
    document_type (
        type_id NUMBER PRIMARY KEY, -- 문서유형 ID (document_type_seq)
        type_name VARCHAR2 (100 CHAR) NOT NULL, -- 문서유형명
        detail_table VARCHAR2(50) -- 'leave', 'expenditure', 'purchase' 등
    );

-- 휴가 유형 (연차, 반차, 조퇴 등)
CREATE TABLE
    leave_type (
        type_id NUMBER PRIMARY KEY, -- 휴가유형 ID (leave_type_seq)
        type_name VARCHAR2 (50) NOT NULL, -- 휴가유형명
        is_paid CHAR(1) NOT NULL, -- 유급 여부 (Y: 유급, N: 무급)
        CONSTRAINT leave_type_ck_is_paid CHECK (is_paid IN ('Y', 'N'))
    );

-- 결재선 (문서유형별 기본 결재자 지정)
CREATE TABLE approval_line (
    approval_line_id NUMBER PRIMARY KEY,    -- 결재선 ID (approval_line_seq)
    document_type    NUMBER NOT NULL,       -- 문서유형 ID (FK → document_type)
    step_order       NUMBER NOT NULL,       -- 결재 단계
    default_approver NUMBER NOT NULL,       -- 기본 결재자 (FK → users)
    CONSTRAINT line_fk_doc_type FOREIGN KEY (document_type) REFERENCES document_type(type_id),
    CONSTRAINT line_fk_approver FOREIGN KEY (default_approver) REFERENCES users(employee_id),
    CONSTRAINT line_uq_step UNIQUE (document_type, step_order) -- 유형별 단계 중복 방지
);

-- 문서 처리 부서 (문서유형별 처리 가능 부서 및 권한 설정)
CREATE TABLE
    document_process (
        process_id NUMBER PRIMARY KEY, -- 처리 ID (document_process_seq)
        document_type NUMBER NOT NULL, -- 문서유형 ID (FK → document_type)
        process_department NUMBER NOT NULL, -- 처리 부서 ID (FK → departments)
        processing_role VARCHAR2 (5 CHAR), -- 처리 가능 역할 (ALL: 전체, ADMIN: 관리자)
        CONSTRAINT process_fk_doc_type FOREIGN KEY (document_type) REFERENCES document_type (type_id),
        CONSTRAINT process_fk_department FOREIGN KEY (process_department) REFERENCES departments (department_id),
        CONSTRAINT process_ck_role CHECK (processing_role IN ('ALL', 'ADMIN'))
    );

-- 결재 문서 (기안서 본문 및 상태 관리)
-- status: TMP(임시저장), REQ(결재요청), APR(승인), REJ(반려), PRC(처리중), COM(완료)
CREATE TABLE document (
    document_id    NUMBER PRIMARY KEY,          -- 문서 ID (approval_document_seq)
    document_type  NUMBER NOT NULL,             -- 문서유형 ID (FK → document_type)
    requester_id   NUMBER NOT NULL,             -- 기안자 ID (FK → users)
    processor_id   NUMBER,                      -- 처리자 ID (FK → users)
    document_title VARCHAR2(200 CHAR) NOT NULL, -- 문서 제목
    status         CHAR(3) DEFAULT 'REQ' NOT NULL, -- 결재 상태
    current_step   NUMBER DEFAULT 1 NOT NULL, -- 현재 결재 단계 (1부터 시작)
    created_at     TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL, -- 생성일시
    updated_at     TIMESTAMP,                   -- 수정일시
    requested_at   TIMESTAMP,                   -- 결재 요청일시
    processed_at   TIMESTAMP,                   -- 처리일시
    reject_reason  VARCHAR2(500 CHAR),          -- 반려 사유 (최종 반려 단계 기준)
    CONSTRAINT document_fk_doc_type FOREIGN KEY (document_type) REFERENCES document_type(type_id),
    CONSTRAINT document_fk_requester FOREIGN KEY (requester_id) REFERENCES users(employee_id),
    CONSTRAINT document_fk_processor FOREIGN KEY (processor_id) REFERENCES users(employee_id),
    CONSTRAINT document_ck_status CHECK (status IN ('TMP', 'REQ', 'APR', 'REJ', 'PRC', 'COM'))
);

-- 결재 이력 (단계별 결재 처리 내역)
-- status: APR(승인), REJ(반려), PND(대기)
CREATE TABLE approval_history (
    history_id       NUMBER PRIMARY KEY,
    document_id      NUMBER NOT NULL,
    step_order       NUMBER NOT NULL,
    approver_id      NUMBER NOT NULL,
    status           CHAR(3) DEFAULT 'PND' NOT NULL,
    approver_comment VARCHAR2(500 CHAR),
    acted_at         TIMESTAMP,
    created_at       TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT history_fk_document FOREIGN KEY (document_id) REFERENCES document(document_id),
    CONSTRAINT history_fk_approver FOREIGN KEY (approver_id) REFERENCES users(employee_id),
    CONSTRAINT history_ck_status CHECK (status IN ('APR', 'REJ', 'PND')),
    CONSTRAINT history_uq_step UNIQUE (document_id, step_order)
);
-- 첨부파일 (업로드된 파일 메타정보 관리)
CREATE TABLE
    attachment (
        attachment_id NUMBER PRIMARY KEY, -- 첨부파일 ID (attachment_seq)
        original_name VARCHAR2 (300 CHAR) NOT NULL, -- 원본 파일명
        stored_name VARCHAR2 (300 CHAR) NOT NULL, -- 서버 저장 파일명 (UUID 등)
        attachment_type VARCHAR2 (100 CHAR) NOT NULL, -- MIME 타입 (예: application/pdf)
        upload_time TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL, -- 업로드일시
        attachment_size NUMBER NOT NULL, -- 파일 크기 (bytes)
        uploader NUMBER NOT NULL, -- 업로더 ID (FK → users)
        CONSTRAINT att_fk_uploader FOREIGN KEY (uploader) REFERENCES users (employee_id)
    );

-- 문서-첨부파일 매핑 (문서와 첨부파일 N:M 관계)
CREATE TABLE
    document_attach_mapping (
        doc_mapping_id NUMBER PRIMARY KEY, -- 매핑 ID (document_attach_mapping_seq)
        document_id NUMBER NOT NULL, -- 문서 ID (FK → document)
        attachment_id NUMBER NOT NULL, -- 첨부파일 ID (FK → attachment)
        CONSTRAINT att_fk_doc_id FOREIGN KEY (document_id) REFERENCES document (document_id),
        CONSTRAINT att_fk_att_id FOREIGN KEY (attachment_id) REFERENCES attachment (attachment_id)
    );

-- 결재 위임 (결재자 부재 시 대리 결재자 지정)
-- is_active: Y(활성), N(비활성) — 기간 만료 후 N으로 업데이트
CREATE TABLE
    approval_delegate (
        approval_delegate_id NUMBER PRIMARY KEY, -- 위임 ID (approval_delegate_seq)
        approver_id NUMBER NOT NULL, -- 원 결재자 ID (FK → users)
        delegate_id NUMBER NOT NULL, -- 대리 결재자 ID (FK → users)
        start_date DATE NOT NULL, -- 위임 시작일
        end_date DATE NOT NULL, -- 위임 종료일
        reason VARCHAR2 (300 CHAR), -- 위임 사유
        is_active CHAR(1) DEFAULT 'Y' NOT NULL, -- 활성 여부
        CONSTRAINT delegate_fk_approver FOREIGN KEY (approver_id) REFERENCES users (employee_id),
        CONSTRAINT delegate_fk_delegate FOREIGN KEY (delegate_id) REFERENCES users (employee_id),
        CONSTRAINT delegate_ck_is_active CHECK (is_active IN ('Y', 'N'))
    );

-- 휴가 신청 (연차/반차/조퇴 등 휴가 신청 데이터)
-- leave_cnt: 사용 휴가 일수 (0.5 단위, 예: 반차=0.5, 조퇴=0.25)
CREATE TABLE
    leave (
        leave_id NUMBER PRIMARY KEY, -- 휴가신청 ID (leave_seq)
        leave_type NUMBER NOT NULL, -- 휴가유형 ID (FK → leave_type)
        document_id NUMBER NOT NULL, -- 연결 결재문서 ID (FK → document)
        start_date DATE NOT NULL, -- 휴가 시작일
        end_date DATE NOT NULL, -- 휴가 종료일
        leave_cnt NUMBER (3, 2) NOT NULL, -- 사용 일수
        CONSTRAINT leave_fk_document FOREIGN KEY (document_id) REFERENCES document (document_id),
        CONSTRAINT leave_fk_type FOREIGN KEY (leave_type) REFERENCES leave_type (type_id),
        CONSTRAINT leave_uq_document UNIQUE (document_id)
    );





---------------------------------------------------------------------------------------
-- 회의실
CREATE TABLE meeting_room (
    room_id     NUMBER          NOT NULL,
    room_name   VARCHAR2(100)   NOT NULL,
    location    VARCHAR2(200)   NULL,
    status      VARCHAR2(20)    DEFAULT 'ACTIVE' NOT NULL,
    CONSTRAINT pk_meeting_room PRIMARY KEY (room_id),
    CONSTRAINT ck_meeting_room_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

COMMENT ON TABLE  meeting_room          IS '회의실';
COMMENT ON COLUMN meeting_room.room_id  IS '회의실 PK';
COMMENT ON COLUMN meeting_room.room_name IS '회의실명';
COMMENT ON COLUMN meeting_room.location IS '위치';
COMMENT ON COLUMN meeting_room.status   IS 'ACTIVE/INACTIVE';


-- 회의실 예약
CREATE TABLE room_reservation (
    reservation_id  NUMBER          NOT NULL,
    room_id         NUMBER          NOT NULL,
    user_id         NUMBER          NOT NULL,
    title           VARCHAR2(200)   NOT NULL,
    rsv_date        DATE            NOT NULL,
    start_time      VARCHAR2(5)     NOT NULL,
    end_time        VARCHAR2(5)     NOT NULL,
    status          VARCHAR2(20)    DEFAULT 'CONFIRMED' NOT NULL,
    purpose         VARCHAR2(500)   NULL,
    create_at       TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT pk_room_reservation PRIMARY KEY (reservation_id),
    CONSTRAINT fk_rsvn_room FOREIGN KEY (room_id) REFERENCES meeting_room (room_id),
    CONSTRAINT fk_rsvn_user FOREIGN KEY (user_id) REFERENCES users (employee_id),
    CONSTRAINT ck_rsvn_status CHECK (status IN ('CONFIRMED', 'CANCELLED'))
);

COMMENT ON TABLE  room_reservation                  IS '회의실 예약';
COMMENT ON COLUMN room_reservation.reservation_id   IS '예약 PK';
COMMENT ON COLUMN room_reservation.room_id          IS '회의실 FK';
COMMENT ON COLUMN room_reservation.user_id          IS '예약자 FK';
COMMENT ON COLUMN room_reservation.title            IS '예약 제목';
COMMENT ON COLUMN room_reservation.rsv_date         IS '예약 날짜';
COMMENT ON COLUMN room_reservation.start_time       IS '시작 시간 HH:MM';
COMMENT ON COLUMN room_reservation.end_time         IS '종료 시간 HH:MM';
COMMENT ON COLUMN room_reservation.status           IS 'CONFIRMED/CANCELLED';
COMMENT ON COLUMN room_reservation.purpose          IS '이용 목적';
COMMENT ON COLUMN room_reservation.create_at        IS '등록일시';


-- 예약 참석자
CREATE TABLE reservation_participant (
    participant_id  NUMBER      NOT NULL,
    reservation_id  NUMBER      NOT NULL,
    user_id         NUMBER      NOT NULL,
    is_organizer    NUMBER(1)   DEFAULT 0 NOT NULL,
    CONSTRAINT pk_reservation_participant PRIMARY KEY (participant_id),
    CONSTRAINT fk_part_rsvn FOREIGN KEY (reservation_id) REFERENCES room_reservation (reservation_id),
    CONSTRAINT fk_part_user FOREIGN KEY (user_id)        REFERENCES users (employee_id),
    CONSTRAINT ck_part_organizer CHECK (is_organizer IN (0, 1))
);

COMMENT ON TABLE  reservation_participant                IS '예약 참석자';
COMMENT ON COLUMN reservation_participant.participant_id IS '참석자 PK';
COMMENT ON COLUMN reservation_participant.reservation_id IS '예약 FK';
COMMENT ON COLUMN reservation_participant.user_id        IS '참석자 FK';
COMMENT ON COLUMN reservation_participant.is_organizer   IS '0=참석자, 1=주최자';


-- 사내메일
CREATE TABLE mail (
    mail_id         NUMBER          NOT NULL,
    user_id         NUMBER          NOT NULL,
    parent_mail_id  NUMBER          NULL,
    title           VARCHAR2(300)   NOT NULL,
    content         CLOB            NOT NULL,
    create_at       TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT pk_mail PRIMARY KEY (mail_id),
    CONSTRAINT fk_mail_user   FOREIGN KEY (user_id)        REFERENCES users (employee_id),
    CONSTRAINT fk_mail_parent FOREIGN KEY (parent_mail_id) REFERENCES mail (mail_id)
);

COMMENT ON TABLE  mail                IS '사내메일';
COMMENT ON COLUMN mail.mail_id        IS '메일 PK';
COMMENT ON COLUMN mail.user_id        IS '발신자 FK';
COMMENT ON COLUMN mail.parent_mail_id IS '답장 원본 FK (자기참조, 최초메일은 NULL)';
COMMENT ON COLUMN mail.title          IS '메일 제목';
COMMENT ON COLUMN mail.content        IS '메일 본문';
COMMENT ON COLUMN mail.create_at      IS '발송일시';


-- 메일 수신자
CREATE TABLE mail_receiver (
    receiver_id NUMBER      NOT NULL,
    mail_id     NUMBER      NOT NULL,
    user_id     NUMBER      NOT NULL,
    is_read     NUMBER(1)   DEFAULT 0 NOT NULL,
    is_deleted  NUMBER(1)   DEFAULT 0 NOT NULL,
    CONSTRAINT pk_mail_receiver PRIMARY KEY (receiver_id),
    CONSTRAINT fk_rcvr_mail FOREIGN KEY (mail_id) REFERENCES mail (mail_id),
    CONSTRAINT fk_rcvr_user FOREIGN KEY (user_id) REFERENCES users (employee_id),
    CONSTRAINT ck_rcvr_read    CHECK (is_read    IN (0, 1)),
    CONSTRAINT ck_rcvr_deleted CHECK (is_deleted IN (0, 1))
);

COMMENT ON TABLE  mail_receiver             IS '메일 수신자';
COMMENT ON COLUMN mail_receiver.receiver_id IS '수신 PK';
COMMENT ON COLUMN mail_receiver.mail_id     IS '메일 FK';
COMMENT ON COLUMN mail_receiver.user_id     IS '수신자 FK';
COMMENT ON COLUMN mail_receiver.is_read     IS '0=안읽음, 1=읽음';
COMMENT ON COLUMN mail_receiver.is_deleted  IS '0=미삭제, 1=삭제';


-- 메일 첨부파일
CREATE TABLE mail_attachment (
    attachment_id   NUMBER          NOT NULL,
    mail_id         NUMBER          NOT NULL,
    original_name   VARCHAR2(300)   NOT NULL,
    saved_name      VARCHAR2(300)   NOT NULL,
    file_path       VARCHAR2(500)   NOT NULL,
    file_size       NUMBER          NOT NULL,
    file_type       VARCHAR2(100)   NOT NULL,
    create_at       TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT pk_mail_attachment PRIMARY KEY (attachment_id),
    CONSTRAINT fk_attach_mail FOREIGN KEY (mail_id) REFERENCES mail (mail_id)
);

COMMENT ON TABLE  mail_attachment               IS '메일 첨부파일';
COMMENT ON COLUMN mail_attachment.attachment_id IS '첨부파일 PK';
COMMENT ON COLUMN mail_attachment.mail_id       IS '메일 FK';
COMMENT ON COLUMN mail_attachment.original_name IS '원본 파일명';
COMMENT ON COLUMN mail_attachment.saved_name    IS 'UUID 저장 파일명';
COMMENT ON COLUMN mail_attachment.file_path     IS '서버 저장 경로';
COMMENT ON COLUMN mail_attachment.file_size     IS '파일 크기(byte)';
COMMENT ON COLUMN mail_attachment.file_type     IS 'MIME 타입';
COMMENT ON COLUMN mail_attachment.create_at     IS '업로드일시';


-- 일정
CREATE TABLE schedule (
    schedule_id     NUMBER          NOT NULL,
    user_id         NUMBER          NOT NULL,
    dept_id         NUMBER          NULL,
    title           VARCHAR2(200)   NOT NULL,
    schedule_type   VARCHAR2(20)    NOT NULL,
    start_dt        DATE            NOT NULL,
    end_dt          DATE            NOT NULL,
    location        VARCHAR2(200)   NULL,
    memo            CLOB            NULL,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT pk_schedule PRIMARY KEY (schedule_id),
    CONSTRAINT fk_sched_user FOREIGN KEY (user_id)  REFERENCES users (employee_id),
    CONSTRAINT fk_sched_dept FOREIGN KEY (dept_id)  REFERENCES departments (department_id),
    CONSTRAINT ck_sched_type CHECK (schedule_type IN ('PERSONAL', 'DEPT', 'COMPANY', 'PROJECT'))
);

COMMENT ON TABLE  schedule              IS '일정';
COMMENT ON COLUMN schedule.schedule_id  IS '일정 PK';
COMMENT ON COLUMN schedule.user_id      IS '작성자 FK';
COMMENT ON COLUMN schedule.dept_id      IS '부서 FK (부서일정일 때)';
COMMENT ON COLUMN schedule.title        IS '일정 제목';
COMMENT ON COLUMN schedule.schedule_type IS 'PERSONAL/DEPT/COMPANY/PROJECT';
COMMENT ON COLUMN schedule.start_dt     IS '시작일';
COMMENT ON COLUMN schedule.end_dt       IS '종료일';
COMMENT ON COLUMN schedule.location     IS '장소';
COMMENT ON COLUMN schedule.memo         IS '메모';
COMMENT ON COLUMN schedule.created_at   IS '등록일시';


-- 공휴일
CREATE TABLE holidays (
    holiday_id      NUMBER          NOT NULL,
    holiday_year    NUMBER(4)       NOT NULL,
    holiday_date    DATE            NOT NULL,
    holiday_name    VARCHAR2(100)   NOT NULL,
    is_substitute   NUMBER(1)       DEFAULT 0 NOT NULL,
    CONSTRAINT pk_holidays PRIMARY KEY (holiday_id),
    CONSTRAINT uq_holidays_date UNIQUE (holiday_date),
    CONSTRAINT ck_holidays_substitute CHECK (is_substitute IN (0, 1))
);

COMMENT ON TABLE  holidays              IS '공휴일';
COMMENT ON COLUMN holidays.holiday_id   IS '공휴일 PK';
COMMENT ON COLUMN holidays.holiday_year IS '연도 (API 캐싱 체크용)';
COMMENT ON COLUMN holidays.holiday_date IS '공휴일 날짜 (UNIQUE)';
COMMENT ON COLUMN holidays.holiday_name IS '공휴일명';
COMMENT ON COLUMN holidays.is_substitute IS '0=일반, 1=대체공휴일';


----------------------------------------------------------------------------------------

--급여 테이블 생성

-- 1. 급여 내역 테이블 생성
CREATE TABLE payrolls (
	payroll_id NUMBER NOT NULL,
	employee_id NUMBER,
	pay_month DATE NOT NULL,
	total_pay NUMBER NOT NULL,
    total_deduction NUMBER NOT NULL,
    net_salary NUMBER NOT NULL,
    status VARCHAR2(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NULL,	
	CONSTRAINT pk_payrolls PRIMARY KEY (payroll_id),
	CONSTRAINT fk_payrolls FOREIGN KEY(employee_id)
	REFERENCES users(employee_id) ON DELETE CASCADE
);


-- 2. 급여 상세 항목 테이블 생성
CREATE TABLE payroll_details (
	payroll_details_id NUMBER NOT NULL,
	payroll_id NUMBER,
	item_type VARCHAR2(10) NOT NULL,
	item_id NUMBER NOT NULL,
	item_name VARCHAR2(50) NOT NULL,
	amount NUMBER NOT NULL,
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NULL,
	CONSTRAINT pk_payroll_details PRIMARY KEY (payroll_details_id),
	CONSTRAINT fk_payroll_details FOREIGN KEY(payroll_id)
	REFERENCES payrolls(payroll_id) ON DELETE CASCADE
);


-- 3. 급여명세서 테이블 생성
CREATE TABLE payslips (
	payslip_id NUMBER NOT NULL,
	payroll_id NUMBER,
	file_name VARCHAR2(100) NOT NULL,
	file_path VARCHAR2(300) NOT NULL,
	file_type VARCHAR2(10) NOT NULL,
	created_at TIMESTAMP NOT NULL,
	CONSTRAINT pk_payslips PRIMARY KEY (payslip_id),
	CONSTRAINT fk_payslips FOREIGN KEY(payroll_id)
	REFERENCES payrolls(payroll_id) ON DELETE CASCADE
);

-- 4. 기본급 정보 테이블 생성
CREATE TABLE salary (
	salary_id NUMBER NOT NULL,
	employee_id NUMBER,
	base_salary NUMBER NOT NULL,
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NULL,
	CONSTRAINT pk_salary PRIMARY KEY (salary_id),
	CONSTRAINT fk_salary FOREIGN KEY(employee_id)
	REFERENCES users(employee_id) ON DELETE CASCADE
);


-- 5. 수당 항목 테이블 생성
CREATE TABLE allowance_items (
	allowance_item_id NUMBER NOT NULL,
	item_name VARCHAR2(50) NOT NULL,
	amount NUMBER NOT NULL,
	CONSTRAINT pk_allowance_items PRIMARY KEY (allowance_item_id)
);


-- 6. 직원 수당 정보 테이블 생성
CREATE TABLE employee_allowance (
	employee_allowance_id NUMBER NOT NULL,
	employee_id NUMBER,
	allowance_item_id NUMBER,
	amount NUMBER NOT NULL,
	CONSTRAINT pk_employee_allowance PRIMARY KEY (employee_allowance_id),
	CONSTRAINT fk_employee_allowance_employee_id FOREIGN KEY(employee_id)
	REFERENCES users(employee_id) ON DELETE CASCADE,
	CONSTRAINT fk_employee_allowance_allowance_item_id FOREIGN KEY(allowance_item_id)
	REFERENCES allowance_items(allowance_item_id) ON DELETE CASCADE
);


-- 7. 공제 항목 테이블 생성
CREATE TABLE deduction_items (
	deduction_item_id NUMBER NOT NULL,
	item_name VARCHAR2(50) NOT NULL,
	ratio NUMBER NOT NULL,
	CONSTRAINT pk_deduction_items PRIMARY KEY(deduction_item_id)
);


-- 8. 직원 공제 정보 테이블 생성
CREATE TABLE employee_deduction (
	employee_deduction_id NUMBER NOT NULL,
	employee_id NUMBER,
	deduction_item_id NUMBER,
	amount NUMBER NOT NULL,
	CONSTRAINT pk_employee_deduction PRIMARY KEY (employee_deduction_id),
	CONSTRAINT fk_employee_deduction_employee_id FOREIGN KEY(employee_id)
	REFERENCES users(employee_id) ON DELETE CASCADE,
	CONSTRAINT fk_employee_deduction_deduction_item_id FOREIGN KEY(deduction_item_id)
	REFERENCES deduction_items(deduction_item_id) ON DELETE CASCADE
);


-- 9. 부양가족 / 원천징수 테이블 생성
CREATE TABLE employee_tax_info (
	tax_info_id NUMBER NOT NULL,
	employee_id NUMBER,
	dependents NUMBER NOT NULL,
	children NUMBER NOT NULL,
	withholding_rate NUMBER NOT NULL,
	created_at TIMESTAMP NOT NULL,
	updated_at TIMESTAMP NULL,
	CONSTRAINT pk_employee_tax_info PRIMARY KEY (tax_info_id),
	CONSTRAINT fk_employee_tax_info FOREIGN KEY(employee_id)
	REFERENCES users(employee_id) ON DELETE CASCADE
);