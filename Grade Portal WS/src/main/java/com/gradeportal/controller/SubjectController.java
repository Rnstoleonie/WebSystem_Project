package com.gradeportal.controller;

import com.gradeportal.dto.SubjectDTO;
import com.gradeportal.entity.Subject;
import com.gradeportal.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        List<Subject> subjects = subjectService.findAllSubjects();
        List<SubjectDTO> subjectDTOs = subjects.stream()
            .map(SubjectDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(subjectDTOs);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createSubject(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String description = request.get("description");

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Subject name is required");
            }

            Subject subject = subjectService.createSubject(name.trim(), 
                description != null ? description.trim() : null);
            return ResponseEntity.ok("Subject created successfully with ID: " + subject.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating subject: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateSubject(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String description = request.get("description");

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Subject name is required");
            }

            Subject subject = subjectService.updateSubject(id, name.trim(), 
                description != null ? description.trim() : null);
            return ResponseEntity.ok("Subject updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating subject: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSubject(@PathVariable Long id) {
        try {
            subjectService.deleteSubject(id);
            return ResponseEntity.ok("Subject deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting subject: " + e.getMessage());
        }
    }
}
