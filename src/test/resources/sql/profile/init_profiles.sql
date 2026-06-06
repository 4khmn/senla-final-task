DELETE FROM ad_images;
DELETE FROM comments;
DELETE FROM messages;
DELETE FROM chats;
DELETE FROM sales_history;
DELETE FROM reviews;
DELETE FROM profiles;
DELETE FROM advertisements;
DELETE FROM categories;
DELETE FROM users;
DELETE FROM favorites;


INSERT INTO users (id, username, password, email, role, rating, enabled, created_at)
VALUES
    (1, 'owner_user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7u41W3u', 'owner@test.com', 'ROLE_USER', 5.0, TRUE, '2026-04-29 10:00:00'),
    (2, 'other_user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7u41W3u', 'other@test.com', 'ROLE_USER', 4.5, FALSE, '2026-04-29 10:00:00'),
    (3, 'third_user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7u41W3u', 'third@test.com', 'ROLE_USER', 4.5, TRUE, '2026-04-29 10:00:00');

INSERT INTO profiles (id, user_id, first_name, last_name, phone, bio, avatar_url)
VALUES
    (1, 1, 'Ivan', 'Ivanov', '+79991112233', 'bio', 'avatars/main.jpg'),
    (2, 2, 'Petr', 'Petrov', '+79994445566', 'bio2', 'avatars/other.jpg'),
    (3, 3, 'Third', 'Thirdovich', '+79994445566', 'bio3', 'avatars/third.jpg');

INSERT INTO categories (id, name, display_name) VALUES (1, 'Bicycles', 'Велосипеды');

INSERT INTO advertisements (id, title, description, price, seller_id, category_id, status, created_at)
VALUES (500, 'My Bike', 'Selling my bike', 1000.00, 1, 1, 'ACTIVE', '2026-04-29 10:00:00'),
       (101, 'Road Bike', 'Old but gold', 300.00, 1, 1, 'SOLD', '2026-04-20 10:00:00'),
       (102, 'Meow', 'meow', 300.00, 3, 1, 'SOLD', '2026-04-20 10:00:00');

INSERT INTO sales_history (id, ad_id, seller_id, buyer_id, final_price, sold_at, was_top)
VALUES (200, 101, 1, 2, 280.00, '2026-04-21 12:00:00', false),
       (201, 102, 3, 2, 280.00, '2026-04-21 12:00:00', true);