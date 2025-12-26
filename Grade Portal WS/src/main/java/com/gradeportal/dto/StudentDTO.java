package com.gradeportal.dto;

import com.gradeportal.entity.Student;

public class StudentDTO {
    
    private Long id;
    private String name;
    private String section;
    private Long userId;
    
    // Constructors
    public StudentDTO() {}
    
    public StudentDTO(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.section = student.getSection();
        this.userId = student.getUser() != null ? student.getUser().getId() : null;
    }
    
    public StudentDTO(Long id, String name, String section, Long userId) {
        this.id = id;
        this.name = name;
        this.section = section;
        this.userId = userId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSection() {
        return section;
    }
    
    public void setSection(String section) {
        this.section = section;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
