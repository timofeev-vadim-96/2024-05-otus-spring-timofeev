--liquibase formatted sql

--changeset timofeev_vadim:2024-09-28-acl-schema
CREATE TABLE IF NOT EXISTS acl_sid
(
    id        BIGSERIAL PRIMARY KEY,
    principal BOOLEAN      NOT NULL,
    sid       VARCHAR(100) NOT NULL,
    UNIQUE (sid, principal)
);

CREATE TABLE IF NOT EXISTS acl_class
(
    id    BIGSERIAL PRIMARY KEY,
    class VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS acl_entry
(
    id                  BIGSERIAL PRIMARY KEY,
    acl_object_identity BIGINT NOT NULL,
    ace_order           INT NOT NULL,
    sid                 BIGINT NOT NULL,
    mask                INT NOT NULL,
    granting            BOOLEAN NOT NULL,
    audit_success       BOOLEAN NOT NULL,
    audit_failure       BOOLEAN NOT NULL,
    UNIQUE (acl_object_identity,ace_order)
);

CREATE TABLE IF NOT EXISTS acl_object_identity
(
    id                 BIGSERIAL PRIMARY KEY,
    object_id_class    BIGINT NOT NULL,
    object_id_identity BIGINT NOT NULL,
    parent_object      BIGINT DEFAULT NULL,
    owner_sid          BIGINT DEFAULT NULL,
    entries_inheriting BOOLEAN NOT NULL,
    UNIQUE (object_id_class,object_id_identity)
);

ALTER TABLE acl_entry
    ADD FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity (id);

ALTER TABLE acl_entry
    ADD FOREIGN KEY (sid) REFERENCES acl_sid (id);

--
-- Constraints for table acl_object_identity
--
ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id);

ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (object_id_class) REFERENCES acl_class (id);

ALTER TABLE acl_object_identity
    ADD FOREIGN KEY (owner_sid) REFERENCES acl_sid (id);