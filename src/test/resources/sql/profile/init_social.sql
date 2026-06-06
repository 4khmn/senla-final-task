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
    (1, 'owner_user', 'pass', 'owner@test.com', 'ROLE_USER', 5.00, TRUE, '2026-04-29 10:00:00'),
    (2, 'buyer_user', 'pass', 'buyer@test.com', 'ROLE_USER', 4.50, TRUE, '2026-04-29 10:00:00');

INSERT INTO profiles (id, user_id, first_name, last_name)
VALUES (1, 1, 'Ivan', 'Ivanov'), (2, 2, 'Petr', 'Petrov');

INSERT INTO categories (id, name, display_name) VALUES (1, 'Bikes', 'Велосипеды');

INSERT INTO advertisements (id, title, description, price, seller_id, category_id, status, created_at)
VALUES (100, 'Specialized Stumpjumper', 'Top bike', 3000.00, 1, 1, 'SOLD', '2026-04-29 11:00:00');

INSERT INTO comments (id, ad_id, author_id, content, created_at)
VALUES (1, 100, 1, 'Is it still available?', '2026-04-30 12:00:00');

INSERT INTO reviews (id, ad_id, seller_id, author_id, score, content, created_at)
VALUES (1, 100, 1, 2, 5, 'Great seller, fast shipping', '2026-04-30 15:00:00');