CREATE TABLE IF NOT EXISTS tb_sys_token (
    id char(32) not null primary key,
    sys_name varchar(255) not null,
    token char(32) not null
);