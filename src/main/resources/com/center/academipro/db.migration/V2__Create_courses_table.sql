CREATE TABLE courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(255) NOT NULL,
    description TEXT,
    image VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL
);