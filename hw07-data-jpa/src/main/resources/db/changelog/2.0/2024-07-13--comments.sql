--liquibase formatted sql

--changeset timofeev_vadim:2024-07-13-comments
create table if not exists comments
(
    id      bigserial,
    text    varchar(255),
    book_id bigint references books (id) on delete cascade,
    primary key (id)
);

