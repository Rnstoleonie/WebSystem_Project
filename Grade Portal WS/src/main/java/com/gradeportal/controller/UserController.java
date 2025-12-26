package com.gradeportal.controller;

import com.gradeportal.entity.User;
import com.gradeportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllTeachers() {
        List<User> teachers = userService.findAllTeachers();
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getPendingUsers() {
        List<User> pendingUsers = userService.findAllPendingUsers();
        return ResponseEntity.ok(pendingUsers);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approveUser(@PathVariable Long id) {
        try {
            userService.approveUser(id);
            return ResponseEntity.ok("User approved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error approving user: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/decline")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> declineUser(@PathVariable Long id) {
        try {
            userService.declineUser(id);
            return ResponseEntity.ok("User declined successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error declining user: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting user: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> assignTeacher(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String className = request.get("className");
            if (className == null || className.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Class name is required");
            }
            
            userService.assignTeacher(id, className);
            return ResponseEntity.ok("Teacher assigned successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error assigning teacher: " + e.getMessage());
        }
    }

    @GetMapping("/current")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<?> getCurrentUser(@RequestParam String username) {
        try {
            Optional<User> user = userService.findByUsername(username);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching user: " + e.getMessage());
        }
    }
}
