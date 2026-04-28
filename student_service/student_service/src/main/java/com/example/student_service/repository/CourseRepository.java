package com.example.student_service.repository;

import com.example.student_service.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for database operations on the Course entity.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Checks whether a course with given code already exists.
     * @param courseCode the course code.
     * @return true if course the with code exists, false otherwise.
     */
    boolean existsByCourseCode(String courseCode);
}
