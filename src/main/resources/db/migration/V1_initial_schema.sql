CREATE TABLE users (
        id BIGSERIAL PRIMARY KEY,
        username VARCHAR(50) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL,
        email VARCHAR(100) NOT NULL UNIQUE,
        role VARCHAR(20) NOT NULL,
        rating NUMERIC(3, 2) DEFAULT 0.00 CHECK (rating >= 0 AND rating <= 5),
        enabled BOOLEAN DEFAULT TRUE
);

CREATE TABLE profiles (
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL UNIQUE,
        first_name VARCHAR(50),
        last_name VARCHAR(50),
        phone VARCHAR(20),
        bio TEXT,
        avatar_url VARCHAR(255),
        CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE categories (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE advertisements (
        id BIGSERIAL PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        description TEXT NOT NULL,
        price NUMERIC(12, 2) NOT NULL,
        seller_id BIGINT NOT NULL,
        category_id BIGINT NOT NULL,
        is_top BOOLEAN DEFAULT FALSE,
        status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, SOLD, DELETED
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_ad_seller FOREIGN KEY (seller_id) REFERENCES users(id),
        CONSTRAINT fk_ad_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE INDEX idx_ads_category ON advertisements(category_id);
CREATE INDEX idx_ads_seller ON advertisements(seller_id);
CREATE INDEX idx_ads_status ON advertisements(status);
CREATE INDEX idx_ads_top ON advertisements(is_top) WHERE is_top = TRUE;

CREATE TABLE ad_images (
        id BIGSERIAL PRIMARY KEY,
        ad_id BIGINT NOT NULL,
        image_url VARCHAR(255) NOT NULL,
        is_primary BOOLEAN DEFAULT FALSE,
        CONSTRAINT fk_image_ad FOREIGN KEY (ad_id) REFERENCES advertisements(id) ON DELETE CASCADE
);

CREATE INDEX idx_ad_images_ad ON ad_images(ad_id);

CREATE TABLE comments (
        id BIGSERIAL PRIMARY KEY,
        ad_id BIGINT NOT NULL,
        author_id BIGINT NOT NULL,
        content TEXT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_comment_ad FOREIGN KEY (ad_id) REFERENCES advertisements(id) ON DELETE CASCADE,
        CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE chats (
        id BIGSERIAL PRIMARY KEY,
        ad_id BIGINT NOT NULL,
        seller_id BIGINT NOT NULL,
        buyer_id BIGINT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_chat_ad FOREIGN KEY (ad_id) REFERENCES advertisements(id),
        CONSTRAINT fk_chat_seller FOREIGN KEY (seller_id) REFERENCES users(id),
        CONSTRAINT fk_chat_buyer FOREIGN KEY (buyer_id) REFERENCES users(id),
        CONSTRAINT unique_ad_chat UNIQUE (ad_id, seller_id, buyer_id)
);

CREATE INDEX idx_chats_user ON chats(buyer_id, seller_id);

CREATE TABLE messages (
        id BIGSERIAL PRIMARY KEY,
        chat_id BIGINT NOT NULL,
        sender_id BIGINT NOT NULL,
        content TEXT NOT NULL,
        sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_msg_chat FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE,
        CONSTRAINT fk_msg_sender FOREIGN KEY (sender_id) REFERENCES users(id)
);
CREATE INDEX idx_messages_chat ON messages(chat_id);

CREATE TABLE sales_history (
        id BIGSERIAL PRIMARY KEY,
        ad_id BIGINT NOT NULL UNIQUE,
        seller_id BIGINT NOT NULL,
        buyer_id BIGINT NOT NULL,
        final_price NUMERIC(12, 2) NOT NULL,
        sold_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_sales_ad FOREIGN KEY (ad_id) REFERENCES advertisements(id),
        CONSTRAINT fk_sales_seller FOREIGN KEY (seller_id) REFERENCES users(id),
        CONSTRAINT fk_sales_buyer FOREIGN KEY (buyer_id) REFERENCES users(id)
);

CREATE TABLE reviews (
        id BIGSERIAL PRIMARY KEY,
        ad_id BIGINT NOT NULL UNIQUE,
        seller_id BIGINT NOT NULL,
        author_id BIGINT NOT NULL,
        score INT NOT NULL CHECK (score >= 1 AND score <= 5),
        content TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_review_ad FOREIGN KEY (ad_id) REFERENCES advertisements(id),
        CONSTRAINT fk_review_seller FOREIGN KEY (seller_id) REFERENCES users(id),
        CONSTRAINT fk_review_author FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE INDEX idx_reviews_seller ON reviews(seller_id);