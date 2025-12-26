package com.gradeportal.service;

import com.gradeportal.entity.Student;
import com.gradeportal.entity.User;
import com.gradeportal.repository.StudentRepository;
import com.gradeportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Student createStudent(String name, String section, Long userId) {
        // Check if student with same name and section already exists
        if (studentRepository.existsByNameAndSection(name, section)) {
            throw new RuntimeException("Student with this name and section already exists");
        }
        
        Student student = new Student();
        student.setName(name);
        student.setSection(section);
        
        if (userId != null) {
            Optional<User> user = userRepository.findById(userId);
            user.ifPresent(student::setUser);
        }
        
        return studentRepository.save(student);
    }
    
    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }
    
    public List<Student> findStudentsByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Student> findStudentsBySection(String section) {
        return studentRepository.findBySection(section);
    }
    
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }
    
    public Optional<Student> findByUser(User user) {
        return studentRepository.findByUser(user);
    }
    
    public Optional<Student> findByUserId(Long userId) {
        return studentRepository.findByUserId(userId);
    }
    
    public List<Student> findAllApprovedStudents() {
        return studentRepository.findApprovedStudents();
    }
    
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
    
    public Student updateStudent(Long id, String name, String section) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setName(name);
            student.setSection(section);
            return studentRepository.save(student);
        }
        throw new RuntimeException("Student not found");
    }
}
