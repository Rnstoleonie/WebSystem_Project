package com.gradeportal.service;

import com.gradeportal.dto.ReportCardDTO;
import com.gradeportal.entity.Grade;
import com.gradeportal.entity.Student;
import com.gradeportal.entity.Subject;
import com.gradeportal.repository.GradeRepository;
import com.gradeportal.repository.StudentRepository;
import com.gradeportal.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GradeService {
    
    @Autowired
    private GradeRepository gradeRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    public Grade assignGrade(Long studentId, Long subjectId, Double gradeValue) {
        Optional<Student> student = studentRepository.findById(studentId);
        Optional<Subject> subject = subjectRepository.findById(subjectId);
        
        if (!student.isPresent()) {
            throw new RuntimeException("Student not found");
        }
        
        if (!subject.isPresent()) {
            throw new RuntimeException("Subject not found");
        }
        
        // Check if grade already exists for this student and subject
        Optional<Grade> existingGrade = gradeRepository.findByStudentAndSubject(student.get(), subject.get());
        if (existingGrade.isPresent()) {
            // Update existing grade
            Grade grade = existingGrade.get();
            grade.setGradeValue(gradeValue);
            grade.setDateAssigned(LocalDate.now());
            return gradeRepository.save(grade);
        }
        
        // Create new grade
        Grade grade = new Grade();
        grade.setStudent(student.get());
        grade.setSubject(subject.get());
        grade.setGradeValue(gradeValue);
        grade.setDateAssigned(LocalDate.now());
        
        return gradeRepository.save(grade);
    }
    
    public List<Grade> findGradesByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }
    
    public List<Grade> findGradesBySubjectId(Long subjectId) {
        return gradeRepository.findBySubjectId(subjectId);
    }
    
    public Optional<Grade> findById(Long id) {
        return gradeRepository.findById(id);
    }
    
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }
    
    public ReportCardDTO generateReportCard(Long studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if (!student.isPresent()) {
            throw new RuntimeException("Student not found");
        }
        
        List<Grade> grades = gradeRepository.findByStudentIdOrderByDateAssignedDesc(studentId);
        
        List<com.gradeportal.dto.GradeDTO> gradeDTOs = grades.stream()
            .map(com.gradeportal.dto.GradeDTO::new)
            .collect(Collectors.toList());
        
        Integer totalGrades = grades.size();
        Integer passedGrades = Math.toIntExact(gradeRepository.countPassingGradesByStudentId(studentId));
        Integer failedGrades = Math.toIntExact(gradeRepository.countFailingGradesByStudentId(studentId));
        
        Double averageGrade = gradeRepository.findAverageGradeByStudentId(studentId);
        if (averageGrade == null) {
            averageGrade = 0.0;
        }
        
        String overallStatus = "N/A";
        if (totalGrades > 0) {
            double passPercentage = (double) passedGrades / totalGrades * 100;
            if (passPercentage >= 70) {
                overallStatus = "Excellent";
            } else if (passPercentage >= 50) {
                overallStatus = "Good";
            } else if (passPercentage >= 30) {
                overallStatus = "Needs Improvement";
            } else {
                overallStatus = "Poor";
            }
        }
        
        return new ReportCardDTO(gradeDTOs, totalGrades, passedGrades, failedGrades, 
                               Math.round(averageGrade * 100.0) / 100.0, overallStatus);
    }
}
