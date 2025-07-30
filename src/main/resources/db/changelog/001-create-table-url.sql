--liquibase formatted sql

--changeset shorturldb:1
CREATE TABLE IF NOT EXISTS url (
    id CHAR(36) PRIMARY KEY,
    alias VARCHAR(300) NOT NULL UNIQUE,
    full_url VARCHAR(10000) NOT NULL UNIQUE,
    short_url VARCHAR(300) NOT NULL UNIQUE
    );
