package com.gradeportal.repository;

import com.gradeportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    List<User> findByRole(User.UserRole role);
    
    List<User> findByStatus(User.UserStatus status);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = 'APPROVED'")
    List<User> findByRoleAndStatusApproved(@Param("role") User.UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.role = 'TEACHER' AND u.assignedClass IS NOT NULL")
    List<User> findAssignedTeachers();
    
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.status = 'APPROVED'")
    List<User> findApprovedStudents();
    
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.id = :userId")
    Optional<User> findStudentByUserId(@Param("userId") Long userId);
    
    Optional<User> findByIdAndRole(Long id, User.UserRole role);
}
