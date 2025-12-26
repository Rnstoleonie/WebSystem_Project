package com.gradeportal.repository;

import com.gradeportal.entity.Student;
import com.gradeportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    List<Student> findByNameContainingIgnoreCase(String name);
    
    List<Student> findBySection(String section);
    
    Optional<Student> findByUser(User user);
    
    @Query("SELECT s FROM Student s WHERE s.user.id = :userId")
    Optional<Student> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT s FROM Student s WHERE s.user.role = 'STUDENT' AND s.user.status = 'APPROVED'")
    List<Student> findApprovedStudents();
    
    boolean existsByNameAndSection(String name, String section);
}
