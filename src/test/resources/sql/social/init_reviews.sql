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
VALUES (1, 'seller_user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7u41W3u', 'seller@test.com', 'ROLE_USER', 5.00, TRUE, '2026-04-30 10:00:00'),
       (2, 'author_user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7u41W3u', 'author@test.com', 'ROLE_USER', 5.00, TRUE, '2026-04-30 10:00:00');

INSERT INTO profiles (id, user_id, first_name, last_name, avatar_url)
VALUES (1, 1, 'firstName', 'lastName', 'avatarUrl'),
       (2, 2, 'firstName', 'lastName', 'avatarUrl');

INSERT INTO categories (id, name, display_name) VALUES (1, 'category', 'category');

INSERT INTO advertisements (id, title, description, price, seller_id, category_id, status, created_at)
VALUES (1, 'title', 'description', 100.00, 1, 1, 'SOLD', '2026-04-30 10:00:00'),
       (2, 'title2', 'description2', 100.00, 1, 1, 'SOLD', '2026-04-30 10:00:00');

INSERT INTO ad_images (id, ad_id, image_url, is_primary)
VALUES (1, 1, 'imageUrl', TRUE);

INSERT INTO sales_history (id, ad_id, seller_id, buyer_id, final_price, sold_at, was_top)
VALUES (1, 1, 1, 2, 280.00, '2026-04-21 12:00:00', false),
       (2, 2, 1, 2, 280.00, '2026-04-21 12:00:00', false);

INSERT INTO reviews (id, ad_id, seller_id, author_id, score, content, created_at)
VALUES (100, 1, 1, 2, 5, 'content', '2026-04-30 11:00:00');
