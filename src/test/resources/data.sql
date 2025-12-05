INSERT INTO profile(name, email, password) VALUES
    ('Admin Bruger', 'admin@example.com', 'testpassword'),
    ('Test Bruger', 'test@example.com', 'testpassword');

INSERT INTO project(title, description, execution_date, service_category, customer_type, creation_date) VALUES
     ('Fliserens', 'Rensning af terrasse', '2025-03-04', 'PAVING_CLEANING', 'PRIVATE_CUSTOMER', '2025-01-12'),
     ('Tagrens', 'Algebehandling af tag', '2025-02-10', 'ROOF_CLEANING', 'BUSINESS_CUSTOMER', '2025-01-12');


INSERT INTO image(url, image_type, is_featured, project_id) VALUES
                                                                ('https://example.com/before1.jpg', 'BEFORE', false, 1),
                                                                ('https://example.com/after1.jpg', 'AFTER', true, 1),
                                                                ('https://example.com/before2.jpg', 'BEFORE', false, 2),
                                                                ('https://example.com/after2.jpg', 'AFTER', true, 2);
