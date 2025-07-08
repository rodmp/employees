create table employee
(
    id                      integer GENERATED ALWAYS AS IDENTITY primary key,
    age     integer,
    birthday      date,
    enabled boolean,
    first_name                varchar(255),
    middle_name                varchar(255),
    last_name                varchar(255),
    second_last_name                varchar(255),
    role                varchar(255),
    sex                varchar(1),
    system_registration timestamp
);

alter table employee owner to test;

insert into employee(AGE,BIRTHDAY,ENABLED,SYSTEM_REGISTRATION,FIRST_NAME,LAST_NAME,MIDDLE_NAME,SECOND_LAST_NAME,SEX,ROLE)
VALUES(32,'2020-01-23',true,'2025-07-08T18:50:58.154323','jose',null,'perez','torres','H','PROGRAMMER');