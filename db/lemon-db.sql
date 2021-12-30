--创建数据库
create database lemon default character set utf8;
grant all privileges on lemon.* to 'lemon'@'%' identified by 'lemon@123';
grant all privileges on lemon.* to 'lemon'@'localhost' identified by 'lemon@123';
flush privileges;

--消息码表
create table lemon_msg_info (
    msg_cd char(8) not null,
    language varchar(15) not null,
    scenario varchar(30) not null default '*',
    msg_info varchar(200) not null,
    create_time datetime not null,
    modifyTime datetime not null
);

alter table lemon_msg_info add primary key (msg_cd, language, scenario);

insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS00001','zh','对不起,系统正忙!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS00002','zh','对不起,系统正忙!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS00003','zh','对不起,签名错误!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS00404','zh','对不起,您访问的资源不存在!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS00401','zh','对不起,您没有认证成功!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS00004','zh','对不起,系统正忙!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS00005','zh','对不起,系统正忙!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS00006','zh','对不起,系统正忙!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS10001','zh','对不起,您输入的数据不合法!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS20000','zh','对不起,系统正忙!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS20001','zh','对不起,系统正忙!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS20002','zh','对不起,系统正忙!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS30001','zh','对不起,非法参数!',now(),now());
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('SYS99999','zh','对不起,系统正忙!',now(),now());
--test
insert into lemon_msg_info(msg_cd,language,msg_info,create_time,modifyTime) 
values ('DM310001','zh','姓名不能为空!',now(),now());