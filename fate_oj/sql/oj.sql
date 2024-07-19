create database if not exists oj;

use oj;

-- create table user
create table if not exists user(
    id bigint primary key auto_increment comment 'id',
    userAccount varchar(50) not null comment '账号',
    userPassword varchar(50) not null comment '密码',
    userName varchar(50) null comment '用户昵称',
    userAvatar varchar(256) null comment '用户头像',
    userProfile varchar(256) null comment '用户简介',
    userRole varchar(50) not null default 'user' comment '用户角色(user/admin/ban)',
    createTime datetime default current_timestamp not null comment '创建时间',
    updateTime datetime default current_timestamp not null on update current_timestamp comment '更新时间',
    isDelete tinyint default 0 not null comment '是否删除',
    index idx_userAccount(userAccount)
) comment '用户表' collate = utf8mb4_general_ci;

-- create table question
/*
 judgeConfig: {
    timeLimit: number,
    memoryLimit: number,
    stackLimit: number
    }
 */
create table if not exists question(
   id bigint primary key auto_increment comment 'id',
    userId bigint not null comment '创建用户 id',
    title varchar(512) not null comment '标题',
    content text not null comment '题目内容',
    tags varchar(1024) null comment '标签列表(json数组)',
    answer text null comment '题目答案',
    submitNum int default 0 not null comment '题目提交次数',
    acceptNum int default 0 not null comment '题目通过次数',
    judgeCase text null comment '判题用例(json数组)',
    judgeConfig text null comment '判题配置(json对象)',
    thumbNum int default 0 not null comment '点赞次数',
    favorNum int default 0 not null comment '收藏次数',
    createTime datetime default current_timestamp not null comment '创建时间',
    updateTime datetime default current_timestamp not null on update current_timestamp comment '更新时间',
    isDelete tinyint default 0 not null comment '是否删除',
    index idx_userId(userId)
)comment '题目表' collate = utf8mb4_general_ci;


-- create table question_submit
/*
   judgeInfo: {
    time: number,
    memory: number,
    message: string,
   }
 */
create table if not exists question_submit(
    id bigint primary key auto_increment comment 'id',
    language varchar(50) not null comment '编程语言',
    code text not null comment '代码',
    judgeInfo text null comment '判题信息(json对象)',
    status int default 0 not null comment '判题状态(0 - 等待判题、1 - 判题中、2 - 成功、3 - 失败)',
    questionId bigint not null comment '题目 id',
    userId bigint not null comment '创建用户 id',
    createTime datetime default current_timestamp not null comment '创建时间',
    updateTime datetime default current_timestamp not null on update current_timestamp comment '更新时间',
    isDelete tinyint default 0 not null comment '是否删除',
    index idx_questionId(questionId),
    index idx_userId(userId)
)comment '题目提交表' collate = utf8mb4_general_ci;
