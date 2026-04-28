package com.example.student_service.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * JPA Entity representing a students enrollment to a course. Acts as a junction table
 * between students and courses.
 */
@Data
@Entity
@Table(name = "enrolments")
public class Enrolment {

    // Auto incremented primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The student who is enrolled. @ManyToOne = many enrolments can belong to one student.
     */
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**
     * The course the student is enrolled to. @ManyToOne = many enrolments can reference the same course.
     */
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Unique invoice reference generated at the point of enrolment.
    private String invoiceNumber;
}