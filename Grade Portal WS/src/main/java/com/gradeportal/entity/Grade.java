package com.gradeportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "grades")
public class Grade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @NotNull
    private Subject subject;
    
    @Column(name = "grade_value", nullable = false)
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double gradeValue;
    
    @Column(name = "date_assigned")
    private LocalDate dateAssigned;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Grade() {}
    
    public Grade(Student student, Subject subject, Double gradeValue) {
        this.student = student;
        this.subject = subject;
        this.gradeValue = gradeValue;
    }
    
    public Grade(Student student, Subject subject, Double gradeValue, LocalDate dateAssigned) {
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
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public Subject getSubject() {
        return subject;
    }
    
    public void setSubject(Subject subject) {
        this.subject = subject;
    }
    
    public Double getGradeValue() {
        return gradeValue;
    }
    
    public void setGradeValue(Double gradeValue) {
        this.gradeValue = gradeValue;
    }
    
    public LocalDate getDateAssigned() {
        return dateAssigned;
    }
    
    public void setDateAssigned(LocalDate dateAssigned) {
        this.dateAssigned = dateAssigned;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (dateAssigned == null) {
            dateAssigned = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return Objects.equals(id, grade.id) &&
               Objects.equals(student, grade.student) &&
               Objects.equals(subject, grade.subject) &&
               Objects.equals(gradeValue, grade.gradeValue);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, student, subject, gradeValue);
    }
    
    @Override
    public String toString() {
        return "Grade{" +
               "id=" + id +
               ", student=" + (student != null ? student.getName() : "null") +
               ", subject=" + (subject != null ? subject.getName() : "null") +
               ", gradeValue=" + gradeValue +
               ", dateAssigned=" + dateAssigned +
               '}';
    }
}
