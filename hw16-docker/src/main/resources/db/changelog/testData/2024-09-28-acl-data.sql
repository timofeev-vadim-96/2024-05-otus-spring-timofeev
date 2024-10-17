--liquibase formatted sql

--changeset timofeev_vadim:2024-09-28-acl-data

--principal - 1 - имя пользователя, 0 - роль
INSERT INTO acl_sid (principal, sid) VALUES
                                         (1, 'admin'),
                                         (1, 'user'),
                                         (0, 'ROLE_ADMIN');

--объекты каких классов будут защищены
INSERT INTO acl_class (class) VALUES
    ('ru.otus.hw.services.dto.BookDto');

--связь между объектом и его владельцем
INSERT INTO acl_object_identity
(object_id_class, object_id_identity,
 parent_object, owner_sid, entries_inheriting)
VALUES
    (1, 1, NULL, 1, 0),
    (1, 2, NULL, 1, 0),
    (1, 3, NULL, 1, 0);

--права пользователей
INSERT INTO acl_entry
(acl_object_identity, ace_order,
 sid, mask, granting, audit_success, audit_failure)
VALUES
    (1, 1, 3, 1, 1, 1, 1), --ROLE_ADMIN может читать 1 книгу
    (1, 2, 2, 1, 1, 1, 1), --user может читать 1 книгу
    (2, 1, 2, 1, 1, 1, 1), --user может читать 2 книгу
    (2, 2, 3, 1, 1, 1, 1), --ROLE_ADMIN может читать 2 книгу
    (3, 1, 3, 1, 1, 1, 1); --ROLE_ADMIN может читать 3 книгу
