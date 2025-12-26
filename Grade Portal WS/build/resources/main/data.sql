-- ===================================================================
-- GRADE PORTAL INITIAL DATA - H2 DATABASE
-- ===================================================================

-- Insert default admin user (password: password)
INSERT INTO users (id, username, password, first_name, last_name, role, status, created_at, updated_at) 
VALUES (1, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'System', 'Administrator', 'ADMIN', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert default teacher user (password: password)
INSERT INTO users (id, username, password, first_name, last_name, role, status, created_at, updated_at) 
VALUES (2, 'teacher1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John', 'Teacher', 'TEACHER', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert default student user (password: password)
INSERT INTO users (id, username, password, first_name, last_name, role, status, created_at, updated_at) 
VALUES (3, 'student1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane', 'Student', 'STUDENT', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert default subjects
INSERT INTO subjects (id, name, description, created_at, updated_at) VALUES (1, 'Mathematics', 'Default Mathematics course covering algebra, geometry, and calculus fundamentals', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO subjects (id, name, description, created_at, updated_at) VALUES (2, 'English Language Arts', 'Default English course focusing on reading, writing, and communication skills', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO subjects (id, name, description, created_at, updated_at) VALUES (3, 'Science', 'Default Science course covering biology, chemistry, and physics concepts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO subjects (id, name, description, created_at, updated_at) VALUES (4, 'History', 'Default History course exploring world history and cultural studies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO subjects (id, name, description, created_at, updated_at) VALUES (5, 'Physical Education', 'Default Physical Education course promoting health and fitness', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO subjects (id, name, description, created_at, updated_at) VALUES (6, 'Computer Science', 'Default Computer Science course introducing programming and technology', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO subjects (id, name, description, created_at, updated_at) VALUES (7, 'Art', 'Default Art course exploring creative expression and visual arts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO subjects (id, name, description, created_at, updated_at) VALUES (8, 'Music', 'Default Music course covering music theory and performance', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample students
INSERT INTO students (id, name, section, user_id, created_at, updated_at) VALUES (1, 'Jane Student', 'Grade 10-A', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample grades for demonstration
INSERT INTO grades (id, student_id, subject_id, grade_value, date_assigned, created_at, updated_at) VALUES (1, 1, 1, 85.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO grades (id, student_id, subject_id, grade_value, date_assigned, created_at, updated_at) VALUES (2, 1, 2, 92.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO grades (id, student_id, subject_id, grade_value, date_assigned, created_at, updated_at) VALUES (3, 1, 3, 88.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
