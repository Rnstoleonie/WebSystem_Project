package com.gradeportal.repository;

import com.gradeportal.entity.Grade;
import com.gradeportal.entity.Student;
import com.gradeportal.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    List<Grade> findByStudent(Student student);
    
    List<Grade> findByStudentId(Long studentId);
    
    List<Grade> findBySubject(Subject subject);
    
    List<Grade> findBySubjectId(Long subjectId);
    
    Optional<Grade> findByStudentAndSubject(Student student, Subject subject);
    
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId ORDER BY g.dateAssigned DESC")
    List<Grade> findByStudentIdOrderByDateAssignedDesc(@Param("studentId") Long studentId);
    
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.gradeValue >= 60")
    List<Grade> findPassingGradesByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.gradeValue < 60")
    List<Grade> findFailingGradesByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT AVG(g.gradeValue) FROM Grade g WHERE g.student.id = :studentId")
    Double findAverageGradeByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.student.id = :studentId")
    Long countGradesByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.student.id = :studentId AND g.gradeValue >= 60")
    Long countPassingGradesByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.student.id = :studentId AND g.gradeValue < 60")
    Long countFailingGradesByStudentId(@Param("studentId") Long studentId);
}
