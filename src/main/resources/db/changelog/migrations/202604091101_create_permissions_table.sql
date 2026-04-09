--liquibase formatted sql

--changeset samuel:202604091101_create_permissions_table.sql
CREATE TABLE IF NOT EXISTS permissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255) NOT NULL,
    user_id INT,
    CONSTRAINT fk_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
);
--rollback DROP CONSTRAINTS fk_user
--rollback DROP TABLE permissions;