--liquibase formatted sql

--changeset timofeev_vadim:2024-09-27-data

insert into authorities(authority)
values ('ROLE_ADMIN'),
       ('ROLE_USER');

insert into users(username, password)
values ('admin', 'admin'),
       ('user', 'user');

insert into users_authorities(user_id, authority_id)
values (1, 1),
       (2, 2);