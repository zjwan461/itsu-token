drop table if exists tb_sys_token;

create table tb_sys_token
(
    id          char(32)     not null primary key,
    sys_name    varchar(255) not null,
    private_key varchar(255) not null,
    public_key  varchar(900) not null
);