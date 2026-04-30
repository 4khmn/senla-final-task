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
    (1, 'seller_user', 'pass', 'seller@test.com', 'ROLE_USER', 5.00, TRUE, '2026-04-29 10:00:00'),
    (2, 'buyer_user', 'pass', 'buyer@test.com', 'ROLE_USER', 4.80, TRUE, '2026-04-29 10:00:00');

INSERT INTO profiles (id, user_id, first_name, last_name, avatar_url)
VALUES
    (1, 1, 'Tech', 'Seller', 'avatars/seller.png'),
    (2, 2, 'Gadget', 'Lover', 'avatars/buyer.png');

INSERT INTO categories (id, name, display_name) VALUES (1, 'Electronics', 'Электроника');

INSERT INTO advertisements (id, title, description, price, seller_id, category_id, status, created_at)
VALUES (500, 'Mirrorless Camera', 'Sony A7 IV', 2500.00, 1, 1, 'ACTIVE', '2026-04-30 08:00:00');

INSERT INTO chats (id, ad_id, seller_id, buyer_id, created_at)
VALUES (1, 500, 1, 2, '2026-04-30 09:00:00');

INSERT INTO messages (id, chat_id, sender_id, content, sent_at, updated_at)
VALUES
    (1, 1, 2, 'Hi! Is the camera still available?', '2026-04-30 09:05:00', '2026-04-30 09:05:00'),
    (2, 1, 1, 'Yes, it is!', '2026-04-30 09:10:00', '2026-04-30 09:05:00');