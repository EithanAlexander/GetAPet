-- Init some data

-- Insert default categories if not exists
INSERT INTO categories (category)
SELECT * FROM (VALUES ('Default'), ('Dog'), ('Cat'), ('Bird'), ('Duck') ,('Reptiles'), ('Fish'), ('Rabbit'), ('Guinea Pigs')
, ('Hamster'), ('Gerbil'), ('Ferret'), ('Hedgehog'), ('Pig'), ('Horse'), ('Ostrich'), ('Turtle')) AS category(category)
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE category = category.category
);

-- Insert default users if not exists
INSERT INTO users (username, password, display_name, email, phone)
SELECT username, password, display_name, email, phone FROM (
    VALUES
    ('Dan', 'password1', 'Dango', 'john@example.com', '555-0100'),
    ('Donna', 'password2', 'Dongo', 'jane@example.com', '555-0101')
) AS userinfo(username, password, display_name, email, phone)
WHERE NOT EXISTS (
    SELECT 1 FROM users u WHERE u.username = userinfo.username
);


-- Insert multiple ads into the ads table if they do not exist
INSERT INTO ads (author_id, category_id, pet_name, pet_age, pet_gender, ad_content, image_path)
SELECT user_id, category_id, pet_name, pet_age, pet_gender, ad_content, image_path
FROM (
    SELECT u.id AS user_id, c.id AS category_id, 'Whiskers' AS pet_name, 6 AS pet_age, 'Female' AS pet_gender, 'Whiskers is very playful and loves to explore every corner of the house. She is also very affectionate and enjoys cuddling' AS ad_content, '/images/Whiskers.jpg' AS image_path FROM users u, categories c WHERE u.username = 'Donna' AND c.category = 'Cat'
    UNION ALL
    SELECT u.id AS user_id, c.id AS category_id, 'Buddy' AS pet_name, 2 AS pet_age, 'Male' AS pet_gender, 'Buddy loves to cuddle and is very friendly. He enjoys playing fetch and running around in the yard' AS ad_content, '/images/Buddy.png' AS image_path FROM users u, categories c WHERE u.username = 'Dan' AND c.category = 'Dog'
    UNION ALL
    SELECT u.id AS user_id, c.id AS category_id, 'Daffy' AS pet_name, 4 AS pet_age, 'Male' AS pet_gender, 'Daffy is a bit shy but very affectionate. He enjoys quiet spaces and loves to swim' AS ad_content, '/images/Daffy.jpg' AS image_path FROM users u, categories c WHERE u.username = 'Donna' AND c.category = 'Duck'
    UNION ALL
    SELECT u.id AS user_id, c.id AS category_id, 'Luna' AS pet_name, 1 AS pet_age, 'Female' AS pet_gender, 'Luna is full of energy and loves to play. She is curious and enjoys climbing and exploring' AS ad_content, '/images/Luna.png' AS image_path FROM users u, categories c WHERE u.username = 'Dan' AND c.category = 'Cat'
    UNION ALL
    SELECT u.id AS user_id, c.id AS category_id, 'Thumper' AS pet_name, 5 AS pet_age, 'Female' AS pet_gender, 'Thumper is very calm and loves to be petted. She enjoys hopping around and nibbling on carrots' AS ad_content, '/images/Thumper.jpg' AS image_path FROM users u, categories c WHERE u.username = 'Donna' AND c.category = 'Rabbit'
    UNION ALL
    SELECT u.id AS user_id, c.id AS category_id, 'Spike' AS pet_name, 3 AS pet_age, 'Male' AS pet_gender, 'Spike likes to explore and is very curious. He enjoys digging and hiding in small spaces' AS ad_content, '/images/Spike.jpg' AS image_path FROM users u, categories c WHERE u.username = 'Donna' AND c.category = 'Hedgehog'
    UNION ALL
    SELECT u.id AS user_id, c.id AS category_id, 'Mittens' AS pet_name, 1 AS pet_age, 'Female' AS pet_gender, 'Mittens loves to play in the snow and is very active. She enjoys chasing laser pointers and playing with yarn' AS ad_content, '/images/Mittens.jpg' AS image_path FROM users u, categories c WHERE u.username = 'Dan' AND c.category = 'Cat'
    UNION ALL
    SELECT u.id AS user_id, c.id AS category_id, 'Bandit' AS pet_name, 4 AS pet_age, 'Male' AS pet_gender, 'Bandit is very brave and protective. He enjoys running through tunnels and playing with toys' AS ad_content, '/images/Bandit.png' AS image_path FROM users u, categories c WHERE u.username = 'Donna' AND c.category = 'Ferret'
    UNION ALL
    SELECT u.id AS user_id, c.id AS category_id, 'Emmanuel ' AS pet_name, 3 AS pet_age, 'Male' AS pet_gender, 'Emmanuel is very elegant and graceful. He enjoys strutting around and showing off his feathers, loves the camera and the camera loves him!' AS ad_content, '/images/Emmanuel.png' AS image_path FROM users u, categories c WHERE u.username = 'Dan' AND c.category = 'Ostrich'
    UNION ALL
    SELECT u.id AS user_id, c.id AS category_id, 'Babe' AS pet_name, 1 AS pet_age, 'Male' AS pet_gender, 'Babe is very friendly and enjoys interacting with people. He loves rolling in the mud and rooting around for treats' AS ad_content, '/images/Babe.jpg' AS image_path FROM users u, categories c WHERE u.username = 'Donna' AND c.category = 'Pig'
    UNION ALL
    SELECT u.id AS user_id, c.id AS category_id, 'Shelly' AS pet_name, 5 AS pet_age, 'Female' AS pet_gender, 'Shelly is very friendly and enjoys interacting with people. She loves basking in the sun and swimming in her tank' AS ad_content, '/images/Shelly.png' AS image_path FROM users u, categories c WHERE u.username = 'Dan' AND c.category = 'Turtle'
) AS new_ads
WHERE NOT EXISTS (
    SELECT 1 FROM ads WHERE pet_name = new_ads.pet_name AND author_id = new_ads.user_id
);




