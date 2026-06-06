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

INSERT INTO categories (id, name, display_name) VALUES
    (1, 'electronics', 'Электроника'),
    (2, 'home_appliances', 'Бытовая техника'),
    (3, 'clothes', 'Одежда');