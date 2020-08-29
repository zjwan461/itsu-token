drop table if exists tb_sys_token;

create table tb_sys_token
(
    id       char(32)     not null primary key,
    sys_name varchar(255) not null,
    token    char(32)     not null
);