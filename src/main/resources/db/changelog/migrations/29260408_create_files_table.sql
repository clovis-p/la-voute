--liquibase formatted sql

--changeset mia:202604082057_create_files_table
CREATE TABLE IF NOT EXISTS files (
    id INT PRIMARY KEY AUTO_INCREMENT,
    path VARCHAR(255) NOT NULL,
    parent_dir_id INT,
    user_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    type_id INT,
    is_directory TINYINT(1) DEFAULT 0 NOT NULL,
    is_locked TINYINT(1) DEFAULT 1 NOT NULL,

    CONSTRAINT fk_files_parent
        FOREIGN KEY (parent_dir_id)
        REFERENCES files(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_files_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

/*    CONSTRAINT fk_files_type
        FOREIGN KEY (type_id)
        REFERENCES file_types(id)*/
)