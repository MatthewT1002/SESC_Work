package com.example.student_service.repository;

import com.example.student_service.model.Enrolment;
import com.example.student_service.model.Student;
import com.example.student_service.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for database operations on the Enrolment entity.
 */

@Repository
public interface EnrolmentRepository extends JpaRepository<Enrolment, Long> {

    // Retrieves all enrolments belonging to a given student
    List<Enrolment> findByStudent(Student student);

    // Checks if student is already enrolled.
    boolean existsByStudentAndCourse(Student student, Course course);

    // Checks total number of courses student is enrolled upon.
    int countByStudent(Student student);
}
