package com.example.student_service.controller;

import com.example.student_service.model.Student;
import com.example.student_service.model.Enrolment;
import com.example.student_service.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentRestController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        try {
            Student student = studentService.getStudentById(id);
            return ResponseEntity.ok(student);
        }
        catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}/enrolments")
    public ResponseEntity<List<Enrolment>> getEnrolments(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(studentService.getEnrolmentsForStudent(id));
        }
        catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/cangraduate")
    public ResponseEntity<Boolean> canGraduate(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(studentService.canGraduate(id));
        }
        catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
