package com.gradeportal.dto;

import java.util.List;

public class ReportCardDTO {
    
    private List<GradeDTO> grades;
    private Integer totalGrades;
    private Integer passedGrades;
    private Integer failedGrades;
    private Double averageGrade;
    private String overallStatus;
    
    // Constructors
    public ReportCardDTO() {}
    
    public ReportCardDTO(List<GradeDTO> grades, Integer totalGrades, Integer passedGrades, 
                        Integer failedGrades, Double averageGrade, String overallStatus) {
        this.grades = grades;
        this.totalGrades = totalGrades;
        this.passedGrades = passedGrades;
        this.failedGrades = failedGrades;
        this.averageGrade = averageGrade;
        this.overallStatus = overallStatus;
    }
    
    // Getters and Setters
    public List<GradeDTO> getGrades() {
        return grades;
    }
    
    public void setGrades(List<GradeDTO> grades) {
        this.grades = grades;
    }
    
    public Integer getTotalGrades() {
        return totalGrades;
    }
    
    public void setTotalGrades(Integer totalGrades) {
        this.totalGrades = totalGrades;
    }
    
    public Integer getPassedGrades() {
        return passedGrades;
    }
    
    public void setPassedGrades(Integer passedGrades) {
        this.passedGrades = passedGrades;
    }
    
    public Integer getFailedGrades() {
        return failedGrades;
    }
    
    public void setFailedGrades(Integer failedGrades) {
        this.failedGrades = failedGrades;
    }
    
    public Double getAverageGrade() {
        return averageGrade;
    }
    
    public void setAverageGrade(Double averageGrade) {
        this.averageGrade = averageGrade;
    }
    
    public String getOverallStatus() {
        return overallStatus;
    }
    
    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }
}
