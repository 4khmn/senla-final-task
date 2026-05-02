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


INSERT INTO users (id, username, password, email, role, rating, enabled, created_at)
VALUES
    (1, 'admin-user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7u41W3u', 'admin@test.com', 'ROLE_ADMIN', 5.0, TRUE, '2026-04-29 10:00:00'),
    (2, 'other_user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7u41W3u', 'other@test.com', 'ROLE_USER', 4.5, FALSE, '2026-04-29 10:00:00'),
    (3, 'third_user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7u41W3u', 'third@test.com', 'ROLE_USER', 4.5, TRUE, '2026-04-29 10:00:00');

INSERT INTO profiles (id, user_id, first_name, last_name, phone, bio, avatar_url)
VALUES
    (1, 1, 'Ivan', 'Ivanov', '+79991112233', 'bio', 'avatars/main.jpg'),
    (2, 2, 'Petr', 'Petrov', '+79994445566', 'bio2', 'avatars/other.jpg'),
    (3, 3, 'Third', 'Thirdovich', '+79994445566', 'bio3', 'avatars/third.jpg');


INSERT INTO categories (id, name, display_name) VALUES (2, 'category', 'category'),
(3, 'category2', 'category2');

INSERT INTO advertisements (id, title, description, price, seller_id, category_id, status, created_at)
VALUES (1, 'title', 'description', 100.00, 3, 2, 'SOLD', '2026-04-30 10:00:00'),
       (2, 'title2', 'description2', 100.00, 3, 2, 'SOLD', '2026-04-30 10:00:00');


INSERT INTO reviews (id, ad_id, seller_id, author_id, score, content, created_at)
VALUES (100, 1, 3, 2, 5, 'content', '2026-04-30 11:00:00'),
       (101, 2, 3, 2, 5, 'content', '2026-04-30 11:00:00');

