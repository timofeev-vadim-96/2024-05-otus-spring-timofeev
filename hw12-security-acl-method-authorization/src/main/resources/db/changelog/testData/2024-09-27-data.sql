--liquibase formatted sql

--changeset timofeev_vadim:2024-09-27-data

insert into authorities(authority) values
('ROLE_ADMIN'), ('ROLE_USER');