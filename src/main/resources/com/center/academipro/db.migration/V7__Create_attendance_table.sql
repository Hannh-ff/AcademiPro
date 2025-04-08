CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    class_id INT NOT NULL,
    student_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('Present', 'Absent') NOT NULL,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);