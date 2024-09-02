CREATE TABLE IF NOT EXISTS users (
    user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(10),
    city VARCHAR(10)
);

INSERT INTO users (name, city) VALUES ('Vasyl', 'Kyiv');