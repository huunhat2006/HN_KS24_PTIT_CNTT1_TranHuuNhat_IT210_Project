USE projectIT210;

DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS borrowing_details;
DROP TABLE IF EXISTS borrowing_records;
DROP TABLE IF EXISTS academic_evaluations;
DROP TABLE IF EXISTS mentoring_sessions;
DROP TABLE IF EXISTS equipments;
DROP TABLE IF EXISTS lecturers;
DROP TABLE IF EXISTS user_profiles;
DROP TABLE IF EXISTS lab_types;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS users;

CREATE TABLE departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE lab_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'LECTURER', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_profiles (
    user_id INT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    student_id_code VARCHAR(20) UNIQUE,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE lecturers (
    user_id INT PRIMARY KEY,
    department_id INT NOT NULL,
    specialization VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE equipments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    equipment_name VARCHAR(150) NOT NULL,
    lab_type_id INT,
    description TEXT,
    total_quantity INT NOT NULL DEFAULT 0,
    available_quantity INT NOT NULL DEFAULT 0,
    FOREIGN KEY (lab_type_id) REFERENCES lab_types(id)
);

CREATE TABLE mentoring_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    lecturer_id INT NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'CANCELED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(user_id),
    UNIQUE KEY unique_lecturer_slot (lecturer_id, session_date, start_time)
);

CREATE TABLE academic_evaluations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT UNIQUE NOT NULL,
    evaluation_notes TEXT,
    performance_rating INT CHECK (performance_rating >= 1 AND performance_rating <= 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES mentoring_sessions(id) ON DELETE CASCADE
);

CREATE TABLE borrowing_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    status ENUM('PENDING_APPROVAL', 'ISSUED', 'RETURNED', 'OVERDUE') DEFAULT 'PENDING_APPROVAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES mentoring_sessions(id)
);

CREATE TABLE borrowing_details (
    borrowing_record_id INT NOT NULL,
    equipment_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (borrowing_record_id, equipment_id),
    FOREIGN KEY (borrowing_record_id) REFERENCES borrowing_records(id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipments(id)
);

CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    borrowing_record_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    status ENUM('PENDING', 'SUCCESS', 'FAILED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (borrowing_record_id) REFERENCES borrowing_records(id)
);