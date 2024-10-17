--liquibase formatted sql

--changeset timofeev_vadim:2024-09-03-users
create table if not exists users
(
    id        bigserial,
    username  varchar(255),
    password  varchar(255),
    authority varchar(255),
    primary key (id)
);

