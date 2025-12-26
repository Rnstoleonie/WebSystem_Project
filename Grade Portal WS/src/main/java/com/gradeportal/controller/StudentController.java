package com.gradeportal.controller;

import com.gradeportal.dto.StudentDTO;
import com.gradeportal.entity.Student;
import com.gradeportal.entity.User;
import com.gradeportal.service.StudentService;
import com.gradeportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<Student> students = studentService.findAllStudents();
        List<StudentDTO> studentDTOs = students.stream()
            .map(StudentDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> createStudent(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String section = request.get("section");
            Long userId = null;
            
            if (request.get("userId") != null) {
                userId = Long.parseLong(request.get("userId"));
            }

            if (name == null || name.trim().isEmpty() || section == null || section.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Name and section are required");
            }

            Student student = studentService.createStudent(name.trim(), section.trim(), userId);
            return ResponseEntity.ok("Student created successfully with ID: " + student.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating student: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> updateStudent(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String section = request.get("section");

            if (name == null || name.trim().isEmpty() || section == null || section.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Name and section are required");
            }

            Student student = studentService.updateStudent(id, name.trim(), section.trim());
            return ResponseEntity.ok("Student updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating student: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok("Student deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting student: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<?> getStudentByUserId(@PathVariable Long userId) {
        try {
            Optional<Student> student = studentService.findByUserId(userId);
            if (student.isPresent()) {
                return ResponseEntity.ok(student.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching student: " + e.getMessage());
        }
    }
}
