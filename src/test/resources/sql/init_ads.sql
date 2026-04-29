DELETE FROM advertisements;
DELETE FROM categories;
DELETE FROM users;

INSERT INTO categories (id, name, display_name) VALUES (1, 'Bicycles', 'Велосипеды');

INSERT INTO users (id, username, password, email, role)
VALUES (1, 'seller1', 'password', 'seller@test.com', 'ROLE_USER');

INSERT INTO advertisements (id, title, description, price, seller_id, category_id, status, created_at)
VALUES (100, 'Горный велосипед', 'Отличный велик', 50000.00, 1, 1, 'ACTIVE', '2026-04-28 10:00:00');
