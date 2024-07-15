--liquibase formatted sql

--changeset timofeev_vadim:2024-07-11-init
create table if not exists authors (
    id bigserial,
    full_name varchar(255),
    primary key (id)
);

create table if not exists genres (
    id bigserial,
    name varchar(255),
    primary key (id)
);

create table if not exists books (
    id bigserial,
    title varchar(255) UNIQUE NOT NULL,
    author_id bigint references authors (id) on delete cascade,
    primary key (id)
);

create table if not exists books_genres (
    book_id bigint references books(id) on delete cascade,
    genre_id bigint references genres(id) on delete cascade,
    primary key (book_id, genre_id)
);