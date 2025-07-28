--liquibase formatted sql

--changeset shorturldb:1
CREATE TABLE IF NOT EXISTS url (
    id CHAR(36) PRIMARY KEY,
    long_url VARCHAR(1000) NOT NULL UNIQUE,
    short_url VARCHAR(10) NOT NULL UNIQUE
    );
