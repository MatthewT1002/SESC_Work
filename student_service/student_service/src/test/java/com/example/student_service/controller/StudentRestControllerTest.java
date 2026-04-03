package com.example.student_service.controller;

import com.example.student_service.model.Enrolment;
import com.example.student_service.model.Student;
import com.example.student_service.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentRestControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentRestController studentRestController;

    private Student student;
    private Enrolment enrolment;

    private static final Long STUDENT_ID = 1L;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(STUDENT_ID);
        student.setUsername("testuser");
        student.setEmail("test@example.com");

        enrolment = new Enrolment();
        enrolment.setStudent(student);
    }

    // -------------------------
    // GET /api/student/{id}
    // -------------------------

    @Test
    void getStudent_Returns200_WhenStudentFound() {
        when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);

        ResponseEntity<Student> response = studentRestController.getStudent(STUDENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(STUDENT_ID, response.getBody().getId());
    }

    @Test
    void getStudent_Returns404_WhenStudentNotFound() {
        when(studentService.getStudentById(STUDENT_ID))
                .thenThrow(new RuntimeException("Student not found"));

        ResponseEntity<Student> response = studentRestController.getStudent(STUDENT_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // -------------------------
    // GET /api/student
    // -------------------------

    @Test
    void getAllStudents_Returns200_WithListOfStudents() {
        when(studentService.getAllStudents()).thenReturn(List.of(student));

        ResponseEntity<List<Student>> response = studentRestController.getAllStudents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(student, response.getBody().get(0));
    }

    @Test
    void getAllStudents_Returns200_WithEmptyList_WhenNoStudentsExist() {
        when(studentService.getAllStudents()).thenReturn(List.of());

        ResponseEntity<List<Student>> response = studentRestController.getAllStudents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // -------------------------
    // GET /api/student/{id}/enrolments
    // -------------------------

    @Test
    void getEnrolments_Returns200_WithListOfEnrolments() {
        when(studentService.getEnrolmentsForStudent(STUDENT_ID)).thenReturn(List.of(enrolment));

        ResponseEntity<List<Enrolment>> response = studentRestController.getEnrolments(STUDENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(enrolment, response.getBody().get(0));
    }

    @Test
    void getEnrolments_Returns200_WithEmptyList_WhenNoEnrolmentsExist() {
        when(studentService.getEnrolmentsForStudent(STUDENT_ID)).thenReturn(List.of());

        ResponseEntity<List<Enrolment>> response = studentRestController.getEnrolments(STUDENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getEnrolments_Returns404_WhenStudentNotFound() {
        when(studentService.getEnrolmentsForStudent(STUDENT_ID))
                .thenThrow(new RuntimeException("Student not found"));

        ResponseEntity<List<Enrolment>> response = studentRestController.getEnrolments(STUDENT_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // -------------------------
    // GET /api/student/{id}/cangraduate
    // -------------------------

    @Test
    void canGraduate_Returns200WithTrue_WhenStudentCanGraduate() {
        when(studentService.canGraduate(STUDENT_ID)).thenReturn(true);

        ResponseEntity<Boolean> response = studentRestController.canGraduate(STUDENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody());
    }

    @Test
    void canGraduate_Returns200WithFalse_WhenStudentCannotGraduate() {
        when(studentService.canGraduate(STUDENT_ID)).thenReturn(false);

        ResponseEntity<Boolean> response = studentRestController.canGraduate(STUDENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody());
    }

    @Test
    void canGraduate_Returns404_WhenStudentNotFound() {
        when(studentService.canGraduate(STUDENT_ID))
                .thenThrow(new RuntimeException("Student not found"));

        ResponseEntity<Boolean> response = studentRestController.canGraduate(STUDENT_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}