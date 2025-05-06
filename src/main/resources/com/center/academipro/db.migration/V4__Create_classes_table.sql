CREATE TABLE classes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(255) NOT NULL UNIQUE,
    course_id INT NOT NULL,
    teacher_id INT,
    start_date DATE ,
    end_date DATE ,
    max_students INT DEFAULT 30,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    CHECK (end_date > start_date)
);