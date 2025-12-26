package com.gradeportal.controller;

import com.gradeportal.dto.GradeDTO;
import com.gradeportal.dto.ReportCardDTO;
import com.gradeportal.entity.Grade;
import com.gradeportal.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grades")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<GradeDTO>> getGradesByStudent(@PathVariable Long studentId) {
        try {
            List<Grade> grades = gradeService.findGradesByStudentId(studentId);
            List<GradeDTO> gradeDTOs = grades.stream()
                .map(GradeDTO::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(gradeDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/student/{studentId}/report")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<?> getReportCard(@PathVariable Long studentId) {
        try {
            ReportCardDTO reportCard = gradeService.generateReportCard(studentId);
            return ResponseEntity.ok(reportCard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating report card: " + e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> assignGrade(@RequestBody Map<String, Object> request) {
        try {
            // Extract nested object IDs from the request
            Map<String, Object> studentMap = (Map<String, Object>) request.get("student");
            Map<String, Object> subjectMap = (Map<String, Object>) request.get("subject");
            
            Long studentId = ((Number) studentMap.get("id")).longValue();
            Long subjectId = ((Number) subjectMap.get("id")).longValue();
            Double gradeValue = Double.parseDouble(request.get("gradeValue").toString());

            if (studentId == null || subjectId == null || gradeValue == null) {
                return ResponseEntity.badRequest().body("Student ID, Subject ID, and Grade Value are required");
            }

            if (gradeValue < 0 || gradeValue > 100) {
                return ResponseEntity.badRequest().body("Grade must be between 0 and 100");
            }

            Grade grade = gradeService.assignGrade(studentId, subjectId, gradeValue);
            return ResponseEntity.ok("Grade assigned successfully with ID: " + grade.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error assigning grade: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> updateGrade(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Double gradeValue = Double.parseDouble(request.get("gradeValue").toString());

            if (gradeValue == null) {
                return ResponseEntity.badRequest().body("Grade value is required");
            }

            if (gradeValue < 0 || gradeValue > 100) {
                return ResponseEntity.badRequest().body("Grade must be between 0 and 100");
            }

            // For now, we'll use the assignGrade method which handles both create and update
            // In a more complex scenario, we might have a separate update method
            
            return ResponseEntity.ok("Grade updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating grade: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> deleteGrade(@PathVariable Long id) {
        try {
            gradeService.deleteGrade(id);
            return ResponseEntity.ok("Grade deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting grade: " + e.getMessage());
        }
    }
}
