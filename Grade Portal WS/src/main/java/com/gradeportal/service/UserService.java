package com.gradeportal.service;

import com.gradeportal.entity.User;
import com.gradeportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User authenticateUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user.get();
        }
        
        return null;
    }
    
    public User signupUser(String username, String password, String firstName, String lastName, User.UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setStatus(User.UserStatus.PENDING);
        
        return userRepository.save(user);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public List<User> findAllTeachers() {
        return userRepository.findByRoleAndStatusApproved(User.UserRole.TEACHER);
    }
    
    public List<User> findAllPendingUsers() {
        return userRepository.findByStatus(User.UserStatus.PENDING);
    }
    
    public void approveUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(User.UserStatus.APPROVED);
            userRepository.save(user);
        }
    }
    
    public void declineUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(User.UserStatus.DECLINED);
            userRepository.save(user);
        }
    }
    
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    public void assignTeacher(Long teacherId, String className) {
        Optional<User> teacherOpt = userRepository.findByIdAndRole(teacherId, User.UserRole.TEACHER);
        if (teacherOpt.isPresent()) {
            User teacher = teacherOpt.get();
            teacher.setAssignedClass(className);
            userRepository.save(teacher);
        }
    }
    
    public Optional<User> findCurrentUser(String username) {
        return userRepository.findByUsername(username);
    }
    
    public List<User> findAllApprovedStudents() {
        return userRepository.findApprovedStudents();
    }
}
