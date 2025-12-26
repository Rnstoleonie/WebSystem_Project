package com.gradeportal.config;

import com.gradeportal.entity.Subject;
import com.gradeportal.entity.User;
import com.gradeportal.repository.SubjectRepository;
import com.gradeportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataLoaderConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner dataLoader() {
        return args -> {
            // Check if data already exists
            if (userRepository.count() == 0) {
                // Create admin user with encoded password
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("password"));
                admin.setFirstName("System");
                admin.setLastName("Administrator");
                admin.setRole(User.UserRole.ADMIN);
                admin.setStatus(User.UserStatus.APPROVED);
                userRepository.save(admin);

                // Create teacher user
                User teacher = new User();
                teacher.setUsername("teacher1");
                teacher.setPassword(passwordEncoder.encode("password"));
                teacher.setFirstName("John");
                teacher.setLastName("Teacher");
                teacher.setRole(User.UserRole.TEACHER);
                teacher.setStatus(User.UserStatus.APPROVED);
                userRepository.save(teacher);

                // Create student user
                User student = new User();
                student.setUsername("student1");
                student.setPassword(passwordEncoder.encode("password"));
                student.setFirstName("Jane");
                student.setLastName("Student");
                student.setRole(User.UserRole.STUDENT);
                student.setStatus(User.UserStatus.APPROVED);
                userRepository.save(student);

                System.out.println("✅ DataLoader: Initial users created successfully");
            }

            // Initialize subjects if not exists
            if (subjectRepository.count() == 0) {
                List<Subject> subjects = List.of(
                    new Subject("Mathematics", "Default Mathematics course covering algebra, geometry, and calculus fundamentals"),
                    new Subject("English Language Arts", "Default English course focusing on reading, writing, and communication skills"),
                    new Subject("Science", "Default Science course covering biology, chemistry, and physics concepts"),
                    new Subject("History", "Default History course exploring world history and cultural studies"),
                    new Subject("Physical Education", "Default Physical Education course promoting health and fitness"),
                    new Subject("Computer Science", "Default Computer Science course introducing programming and technology"),
                    new Subject("Art", "Default Art course exploring creative expression and visual arts"),
                    new Subject("Music", "Default Music course covering music theory and performance")
                );

                subjectRepository.saveAll(subjects);
                System.out.println("✅ DataLoader: Default subjects created successfully");
            }

            System.out.println("🎉 Grade Portal data initialization completed!");
        };
    }
}
