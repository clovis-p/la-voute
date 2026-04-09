--liquibase formatted sql

--changeset samuel:202604081318_create_users_table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    is_admin TINYINT(1) DEFAULT 0 NOT NULL,
    profile_pic VARCHAR(255)
)
