--liquibase formatted sql

--changeset timofeev_vadim:2024-07-11-data

insert into comments (text, book_id)
values ('comment for 1st book', 1),
       ('comment for 2st book', 2),
       ('comment for 3st book', 3);
