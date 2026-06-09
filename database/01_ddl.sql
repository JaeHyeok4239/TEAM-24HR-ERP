-- 1. 부서 테이블
CREATE TABLE departments (
    department_id    NUMBER          NOT NULL,
    department_code  VARCHAR2(50)    NOT NULL,
    department_name  VARCHAR2(100)   NOT NULL,
    parent_department_id  NUMBER,
    description      VARCHAR2(255),
    is_active        CHAR(1)         DEFAULT 'Y' NOT NULL,
    created_at       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP,

    CONSTRAINT pk_departments PRIMARY KEY (department_id),
    CONSTRAINT uk_departments_code UNIQUE (department_code),
    CONSTRAINT fk_departments_parent FOREIGN KEY (parent_department_id)
        REFERENCES departments(department_id),
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