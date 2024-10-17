--liquibase formatted sql

--changeset timofeev_vadim:2024-09-27-authorities
create table if not exists authorities
(
    id bigserial,
    authority varchar(255),
    primary key (id)
);

create table if not exists users_authorities
(
    id bigserial,
    user_id bigint references users (id),
    authority_id bigint references authorities (id)
);

ALTER TABLE users DROP COLUMN authority;