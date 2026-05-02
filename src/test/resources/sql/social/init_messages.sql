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
VALUES (1, 'sender_user', 'pass', 'sender@test.com', 'ROLE_USER', 5.00, TRUE, '2026-04-30 10:00:00'),
       (2, 'receiver_user', 'pass', 'receiver@test.com', 'ROLE_USER', 5.00, TRUE, '2026-04-30 10:00:00');

INSERT INTO profiles (id, user_id, first_name, last_name, avatar_url)
VALUES (1, 1, 'firstName', 'lastName', 'avatarUrl'),
       (2, 2, 'firstName', 'lastName', 'avatarUrl');

INSERT INTO categories (id, name, display_name) VALUES (1, 'category', 'category');

INSERT INTO advertisements (id, title, description, price, seller_id, category_id, status, created_at)
VALUES (1, 'title', 'description', 100.00, 2, 1, 'ACTIVE', '2026-04-30 10:00:00');

INSERT INTO ad_images (id, ad_id, image_url, is_primary)
VALUES (1, 1, 'imageUrl', TRUE);

INSERT INTO chats (id, ad_id, seller_id, buyer_id, created_at)
VALUES (1, 1, 2, 1, '2026-04-30 10:00:00');

INSERT INTO messages (id, chat_id, sender_id, content, sent_at, updated_at)
VALUES (100, 1, 1, 'content', '2026-04-30 10:00:00', '2026-04-30 10:00:00');