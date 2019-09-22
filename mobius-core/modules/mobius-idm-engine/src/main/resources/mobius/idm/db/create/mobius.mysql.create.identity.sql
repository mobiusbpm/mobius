create table MBS_USER_ACCOUNT (
    ID_ varchar(255),
    USER_LOGIN_NAME varchar(255),
    USER_EMAIL varchar(255),
    USER_PASSWORD varchar(255),
    USER_STATUS_CODE_ID int(2),
    USER_AUTH_TYPE_CODE_ID int(2),
    USER_CREATED_BY varchar(255),
    USER_CREATED_TIME datetime,
    USER_UPDATED_BY varchar(255),
    USER_UPDATED_TIME datetime,
    primary key (ID_)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;

create index MBS_IDX_USER_LOGIN_NAME on MBS_USER_ACCOUNT(USER_LOGIN_NAME);
create index MBS_IDX_USER_EMAIL on MBS_USER_ACCOUNT(USER_EMAIL);

alter table MBS_USER_ACCOUNT add constraint MBS_UNIQ_USER_LOGIN_NAME unique (USER_LOGIN_NAME);
alter table MBS_USER_ACCOUNT add constraint MBS_UNIQ_USER_EMAIL unique (USER_EMAIL);
