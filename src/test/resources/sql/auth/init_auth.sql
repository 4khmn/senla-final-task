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
VALUES (1, 'existing_user', '$2a$10$FjtuW3zFhXpZ0kNznKJ23OMyGQSUeWqLcMbYmNACgaY3H6zIcwgV6', 'existing@test.com', 'ROLE_USER', 5.0, TRUE, '2026-05-02 10:00:00');

INSERT INTO profiles (id, user_id, first_name, last_name)
VALUES (1, 1, 'Existing', 'User');

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('profiles_id_seq', (SELECT MAX(id) FROM profiles));