package com.gradeportal.config;

import com.gradeportal.entity.Subject;
import com.gradeportal.entity.User;
import com.gradeportal.repository.SubjectRepository;
import com.gradeportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class DataLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner dataLoader() {
        return args -> {
            System.out.println("Starting data initialization...");
            
            // Wait a moment for the application to fully start
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Create default admin user if it doesn't exist
            try {
                List<User> existingUsers = userRepository.findAll();
                boolean adminExists = existingUsers.stream()
                    .anyMatch(user -> "admin".equals(user.getUsername()));
                
                if (!adminExists) {
                    User adminUser = new User();
                    adminUser.setUsername("admin");
                    adminUser.setPassword(passwordEncoder.encode("admin123"));
                    adminUser.setFirstName("System");
                    adminUser.setLastName("Administrator");
                    adminUser.setRole(User.UserRole.ADMIN);
                    adminUser.setStatus(User.UserStatus.APPROVED);
                    
                    userRepository.save(adminUser);
                    System.out.println("Default admin user created - Username: admin, Password: admin123");
                } else {
                    System.out.println("Admin user already exists");
                }
            } catch (Exception e) {
                System.out.println("Error checking/creating admin user: " + e.getMessage());
                e.printStackTrace();
            }

            // Create default subjects if they don't exist
            try {
                List<Subject> existingSubjects = subjectRepository.findAll();
                
                String[] defaultSubjects = {
                    "Mathematics", "English", "Science", "History", 
                    "Physical Education", "Computer Science", "Art", "Music"
                };

                for (String subjectName : defaultSubjects) {
                    boolean subjectExists = existingSubjects.stream()
                        .anyMatch(subject -> subjectName.equals(subject.getName()));
                        
                    if (!subjectExists) {
                        Subject subject = new Subject();
                        subject.setName(subjectName);
                        subject.setDescription("Default " + subjectName + " course");
                        subjectRepository.save(subject);
                        System.out.println("Default subject created: " + subjectName);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error creating subjects: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("Data initialization completed!");
        };
    }
}
