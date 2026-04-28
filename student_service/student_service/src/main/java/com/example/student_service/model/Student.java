package com.example.student_service.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * JPA Entity representing a registered student.
 * Maps to the 'students' table in MySQL.
 */

@Data
@Entity
@Table(name = "students")
public class Student {

    // Auto incremented primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Human facing human identifier.
    @Column(unique = true, nullable = false)
    private String studentId;

    // The students chosen username.
    @Column(nullable = false)
    private String username;

    // The students email.
    @Column(unique = true, nullable = false)
    private String email;

    // The students password (stored in plain text TODO: encrypt password at later date).
    @Column(nullable = false)
    private String password;

    // The students first name.
    private  String firstName;

    // The students last name.
    private String lastName;

    // The students age.
    private Integer age;
}