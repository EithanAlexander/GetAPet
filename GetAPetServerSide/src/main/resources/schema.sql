-- Init all tables

-- Table for users, the ID won't be consistent if creating a user fails
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(255) UNIQUE NOT NULL
);

-- Table for categories, the ID won't be consistent if creating a category fails
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    category VARCHAR(255) UNIQUE NOT NULL
);

-- Table for ads, the ID won't be consistent if creating an ad fails
CREATE TABLE IF NOT EXISTS ads (
    id SERIAL PRIMARY KEY,
    author_id INTEGER NOT NULL REFERENCES users(id),
    category_id INTEGER NOT NULL REFERENCES categories(id),
    pet_name VARCHAR(255) NOT NULL,
    pet_age INTEGER CHECK (pet_age BETWEEN 0 AND 100),
    pet_gender VARCHAR(50) NOT NULL,
    ad_content VARCHAR(500) NOT NULL CHECK (ad_content <> ''),
    image_path VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC')
);

CREATE TABLE IF NOT EXISTS favorites (
    user_id INTEGER NOT NULL REFERENCES users(id),
    ad_id INTEGER NOT NULL REFERENCES ads(id),
    PRIMARY KEY (user_id, ad_id)
);



