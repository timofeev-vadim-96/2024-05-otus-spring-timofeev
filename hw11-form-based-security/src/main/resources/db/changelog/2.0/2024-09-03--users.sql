--liquibase formatted sql

--changeset timofeev_vadim:2024-09-03-users
create table if not exists users
(
    id          bigserial,
    principal   varchar(255),
    credentials varchar(255),
    authority   varchar(255),
    primary key (id)
);

