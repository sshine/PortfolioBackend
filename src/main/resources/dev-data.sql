INSERT INTO profile(name, email, password) VALUES
    ('Dev Bruger', 'dev@example.com', 'devpassword');

INSERT INTO project(title, description, execution_date, service_category, customer_type, creation_date)
VALUES ('Dev projekt', 'Beskrivelse', '2025-03-04', 'PAVING_CLEANING', 'PRIVATE_CUSTOMER', '2025-01-01');

INSERT INTO image(url, image_type, is_featured, project_id)
VALUES ('https://example.com/dev.jpg', 'BEFORE', false, 1);