INSERT INTO categories (name, display_name) VALUES
    ('ELECTRONICS', 'Электроника'),
    ('PROPERTY', 'Недвижимость'),
    ('SERVICES', 'Услуги'),
    ('HOBBIES', 'Хобби и отдых'),
    ('TRANSPORT', 'Транспорт');

INSERT INTO users (username, password, email, role, rating, enabled) VALUES
    ('tech_broker', '$2a$10$f/1xk1cYFK3RWey1Ub7EZeB9/55lMt/ex2ogeXTAoBOVi6yRUjFhC', 'tech@mail.com', 'ROLE_USER', 4.90, TRUE),
    ('realtor_pro', '$2a$10$f/1xk1cYFK3RWey1Ub7EZeB9/55lMt/ex2ogeXTAoBOVi6yRUjFhC', 'realty@mail.com', 'ROLE_USER', 4.50, TRUE),
    ('alice_wonder', '$2a$10$f/1xk1cYFK3RWey1Ub7EZeB9/55lMt/ex2ogeXTAoBOVi6yRUjFhC', 'alice@mail.com', 'ROLE_USER', 3.20, TRUE),
    ('bob_investor', '$2a$10$f/1xk1cYFK3RWey1Ub7EZeB9/55lMt/ex2ogeXTAoBOVi6yRUjFhC', 'bob@mail.com', 'ROLE_USER', 2.00, TRUE),
    ('car_lover', '$2a$10$f/1xk1cYFK3RWey1Ub7EZeB9/55lMt/ex2ogeXTAoBOVi6yRUjFhC', 'car@mail.com', 'ROLE_USER', 0.00, TRUE);

INSERT INTO profiles (user_id, first_name, last_name, phone, bio) VALUES
    (2, 'Дмитрий', 'Техно', '89001112233', 'Продажа и ремонт техники Apple и игровых ПК.'),
    (3, 'Елена', 'Риэлтор', '89112223344', 'Помощь в подборе жилья и юридическое сопровождение.'),
    (4, 'Алиса', 'Лисицына', '89223334455', 'Продаю вещи из личной коллекции, книги и декор.'),
    (5, 'Борис', 'Бобров', '89334445566', 'Интересуюсь редкими гаджетами и недвижимостью.'),
    (6, 'Максим', 'Скорость', '89445556677', 'Автоподбор и продажа запчастей.');

INSERT INTO advertisements (title, description, price, seller_id, category_id, is_top, status, top_until) VALUES
    ('iPhone 15 Pro Max', '256 ГБ, титановый синий. Идеальное состояние.', 105000.00, 2, 1, TRUE, 'ACTIVE', NOW() + INTERVAL '5 days'),
    ('Игровая станция RTX 4080', 'Core i9, 32GB RAM, SSD 2TB. Тянет всё на ультрах.', 210000.00, 2, 1, FALSE, 'ACTIVE', NULL),
    ('iPad Pro 12.9 M2', 'Экран Liquid Retina XDR. В комплекте Apple Pencil.', 85000.00, 2, 1, FALSE, 'SOLD', NULL),
    ('Студия в ЖК "Светлый"', '28 м², свежий ремонт, мебель остается.', 4200000.00, 3, 2, TRUE, 'ACTIVE', NOW() + INTERVAL '10 days'),
    ('Дача у озера', '6 соток, уютный домик, плодовые деревья.', 12000.00, 3, 2, FALSE, 'ACTIVE', NULL),
    ('Гараж капитальный', 'Охрана, свет, яма для ремонта.', 350000.00, 3, 2, FALSE, 'ACTIVE', NULL),
    ('Разработка Telegram ботов', 'Пишу на Python (Aiogram), быстрые сроки.', 5000.00, 4, 3, FALSE, 'ACTIVE', NULL),
    ('Уроки английского Online', 'Уровень C1, опыт 5 лет. Подготовка к IELTS.', 1500.00, 4, 3, FALSE, 'ACTIVE', NULL),
    ('Клининг квартир', 'Профессиональная уборка после ремонта.', 3000.00, 4, 3, TRUE, 'ACTIVE', NOW() + INTERVAL '3 days'),
    ('Набор настольных игр', 'Catan, Carcassonne, Dixit. Состояние отличное.', 7000.00, 5, 4, FALSE, 'ACTIVE', NULL),
    ('Коллекция винила 80-х', 'Около 20 пластинок зарубежного рока.', 12000.00, 5, 4, FALSE, 'ACTIVE', NULL),
    ('Электрогитара Yamaha', 'Для начинающих. С комбоусилителем.', 18000.00, 5, 4, FALSE, 'SOLD', NULL),
    ('BMW 320i 2021', 'Пробег 35к, один владелец, без ДТП.', 3800000.00, 6, 5, TRUE, 'ACTIVE', NOW() + INTERVAL '14 days'),
    ('Зимняя резина R17', 'Nokian Hakkapeliitta, остаток 80%.', 25000.00, 6, 5, FALSE, 'ACTIVE', NULL),
    ('Мотоцикл Honda CB400', 'Обслужен, готов к сезону. Новый аккум.', 280000.00, 6, 5, FALSE, 'ACTIVE', NULL);

INSERT INTO comments (ad_id, author_id, content, is_pinned) VALUES
    (1, 5, 'Обмен на MacBook интересен?', FALSE),
    (1, 2, 'Нет, только продажа.', TRUE),
    (4, 6, 'А какой этаж? В описании не указано.', FALSE),
    (4, 3, 'Этаж 12 из 17, окна во двор.', FALSE),
    (7, 5, 'Делаете ботов с оплатой внутри?', FALSE),
    (13, 3, 'Можно вин-номер в личку?', FALSE),
    (13, 6, 'Отправил в сообщения.', FALSE),
    (2, 4, 'Подскажите модель блока питания?', FALSE);

INSERT INTO chats (ad_id, seller_id, buyer_id) VALUES
    (1, 2, 5),
    (4, 3, 5),
    (13, 6, 2),
    (10, 5, 4),
    (12, 5, 4),
    (2, 2, 4);

INSERT INTO messages (chat_id, sender_id, content) VALUES
    (1, 5, 'Привет! За 100 заберу сегодня.'),
    (1, 2, 'Маловато, давай хотя бы 103.'),
    (1, 5, 'Ок, где встретимся?'),
    (2, 5, 'Добрый день, когда можно посмотреть квартиру?'),
    (2, 3, 'Завтра в любое время после обеда.'),
    (3, 2, 'Торг у капота будет?'),
    (3, 6, 'Символический, машина и так по низу рынка.'),
    (4, 4, 'Игры еще не продали?'),
    (5, 4, 'Гитара еще в наличии?'),
    (5, 5, 'Да, приезжайте. Адрес в профиле.'),
    (6, 4, 'В рассрочку отдадите системник?'),
    (6, 2, 'К сожалению, нет.');

INSERT INTO sales_history (ad_id, seller_id, buyer_id, final_price, was_top) VALUES
    (3, 2, 5, 82000.00, FALSE),
    (12, 5, 4, 17500.00, FALSE);

INSERT INTO reviews (ad_id, seller_id, author_id, score, content) VALUES
    (3, 2, 5, 5, 'Продавец отличный, планшет как новый. Спасибо!'),
    (12, 5, 4, 4, 'Гитара ок, но струны пришлось менять сразу. В целом довольна.');