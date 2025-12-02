DROP TABLE IF EXISTS image;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS profile;

CREATE TABLE profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255)

);

CREATE TABLE project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(2000),
    execution_date DATE,
    service_category VARCHAR(255),
    customer_type VARCHAR(255),
    creation_date DATE

);

CREATE TABLE image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(500),
    image_type VARCHAR(255),
    is_featured BOOLEAN,
    project_id BIGINT,
    CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES PROJECT(id)
);