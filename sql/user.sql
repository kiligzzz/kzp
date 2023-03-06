
######################### user库 #########################
CREATE DATABASE IF NOT EXISTS user;

USE user;

# user表
CREATE TABLE IF NOT EXISTS user
(
    id           int(8) PRIMARY KEY AUTO_INCREMENT,
    role_id      tinyint(1)         NOT NULL,
    username     varchar(16) UNIQUE NOT NULL,
    password     varchar(255)       NOT NULL,
    name         varchar(32)        NOT NULL,
    phone        varchar(16) UNIQUE,
    email        varchar(32) UNIQUE,
    wechat       varchar(32) UNIQUE,
    sms_quota    int(8)     DEFAULT 10,
    email_quota  int(8)     DEFAULT 10,
    wechat_quota int(8)     DEFAULT 100,
    fei_quota    int(8)     DEFAULT 100,
    ding_quota   int(8)     DEFAULT 100,
    delete_flag  tinyint(1) DEFAULT 0,
    created_time timestamp  DEFAULT current_timestamp,
    updated_time timestamp  DEFAULT current_timestamp
);

# role表
CREATE TABLE IF NOT EXISTS role
(
    id         tinyint(1) PRIMARY KEY AUTO_INCREMENT,
    role       varchar(16) UNIQUE NOT NULL,
    permission varchar(255)       NOT NULL
);



INSERT INTO user
values (1, 1, 'super-admin', '$2a$10$jYe/4PlQcxclcJdbHpIWkeF3r5jAbMpX2J8bbghpG6Qe6jygGRzK2',
        'super-admin', '17724888002',
        'zyfkilig@gmail.com', '17724888002',
        null, null, null, null, null, null, null, null);

INSERT INTO user
values (2, 2, 'admin', '$2a$10$jYe/4PlQcxclcJdbHpIWkeF3r5jAbMpX2J8bbghpG6Qe6jygGRzK2',
        'admin', null,
        null, null,
        null, null, null, null, null, null, null, null);

INSERT INTO user
values (3, 3, 'user', '$2a$10$jYe/4PlQcxclcJdbHpIWkeF3r5jAbMpX2J8bbghpG6Qe6jygGRzK2',
        'user', null,
        null, null,
        null, null, null, null, null, null, null, null);


INSERT INTO role
values (1, 'SUPER_ADMIN', '*');
INSERT INTO role
values (2, 'ADMIN', 'admin,auth');
INSERT INTO role
values (3, 'USER', 'auth');