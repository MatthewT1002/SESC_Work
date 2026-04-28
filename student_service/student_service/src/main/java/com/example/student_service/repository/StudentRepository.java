package com.example.student_service.repository;

import com.example.student_service.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for database operations on the Student entity.
 */

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Finds student by username.
    Optional<Student> findByUsername(String username);

    // Finds student by email
    Optional<Student> findByEmail(String email);

    // Checks if student with same username exists.
    boolean existsByUsername(String username);

    // Checks if student wit same email exists.
    boolean existsByEmail(String email);

}
