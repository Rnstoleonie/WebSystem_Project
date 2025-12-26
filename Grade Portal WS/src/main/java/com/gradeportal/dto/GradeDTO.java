package com.gradeportal.dto;

import com.gradeportal.entity.Grade;
import com.gradeportal.entity.Student;
import com.gradeportal.entity.Subject;

public class GradeDTO {
    
    private Long id;
    private StudentDTO student;
    private SubjectDTO subject;
    private Double gradeValue;
    private String dateAssigned;
    
    // Constructors
    public GradeDTO() {}
    
    public GradeDTO(Grade grade) {
        this.id = grade.getId();
        this.student = new StudentDTO(grade.getStudent());
        this.subject = new SubjectDTO(grade.getSubject());
        this.gradeValue = grade.getGradeValue();
        this.dateAssigned = grade.getDateAssigned() != null ? grade.getDateAssigned().toString() : null;
    }
    
    public GradeDTO(Long id, StudentDTO student, SubjectDTO subject, Double gradeValue, String dateAssigned) {
        this.id = id;
        this.student = student;
        this.subject = subject;
        this.gradeValue = gradeValue;
        this.dateAssigned = dateAssigned;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public StudentDTO getStudent() {
        return student;
    }
    
    public void setStudent(StudentDTO student) {
        this.student = student;
    }
    
    public SubjectDTO getSubject() {
        return subject;
    }
    
    public void setSubject(SubjectDTO subject) {
        this.subject = subject;
    }
    
    public Double getGradeValue() {
        return gradeValue;
    }
    
    public void setGradeValue(Double gradeValue) {
        this.gradeValue = gradeValue;
    }
    
    public String getDateAssigned() {
        return dateAssigned;
    }
    
    public void setDateAssigned(String dateAssigned) {
        this.dateAssigned = dateAssigned;
    }
}
