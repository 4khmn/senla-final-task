DELETE FROM advertisements;
DELETE FROM categories;
DELETE FROM users;

INSERT INTO users (id, username, password, email, role)
    VALUES (1, 'seller1', 'password', 'seller@test.com', 'ROLE_USER');

INSERT INTO categories (id, name, display_name) VALUES
    (1, 'electronics', 'Электроника'),
    (2, 'home_appliances', 'Бытовая техника');

INSERT INTO advertisements (id, title, description, price, seller_id, category_id, status, created_at) VALUES
    (1, 'Смартфон флагман 2024', 'Новый телефон', 80000.00, 1, 1, 'ACTIVE', '2026-04-01 10:00:00'),
    (2, 'Смартфон ультра люкс', 'Золотой корпус', 250000.00, 1, 1, 'ACTIVE', '2026-04-02 10:00:00'),
    (3, 'Умный пылесос-смартфон', 'С управлением с телефона', 60000.00, 1, 2, 'ACTIVE', '2026-04-03 10:00:00'),
    (4, 'Смартфон б/у', 'В хорошем состоянии', 40000.00, 1, 1, 'SOLD', '2026-04-04 10:00:00'),
    (5, 'Случайное объявление', '', 60000.00, 1, 2, 'ACTIVE', '2026-04-04 10:00:00');