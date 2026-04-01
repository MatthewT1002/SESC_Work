package com.example.student_service.repository;

import com.example.student_service.model.Enrolment;
import com.example.student_service.model.Student;
import com.example.student_service.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrolmentRepository extends JpaRepository<Enrolment, Long> {
    List<Enrolment> findByStudent(Student student);
    boolean existsByStudentAndCourse(Student student, Course course);
    int countByStudent(Student student);
}
