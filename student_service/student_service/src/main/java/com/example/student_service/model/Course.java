package com.example.student_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  JPA Entity representing a university course available for student enrolment.
 *  Maps to the 'courses' table in the MySQL database.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course {

    // Auto incremented primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique course code used to identify course.
    @Column(nullable = false, unique = true)
    private String courseCode;

    // The fee charged to a student upon enrolment.
    @Column(nullable = false)
    private Double price;

    // Human-readable course name i.e "Introduction to Programming".
    @Column(nullable = false)
    private String courseName;

    // Description of course (optional).
    private String courseDescription;

}
