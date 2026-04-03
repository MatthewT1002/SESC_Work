package com.example.student_service.service;

import com.example.student_service.client.FinanceClient;
import com.example.student_service.client.LibraryClient;
import com.example.student_service.model.Course;
import com.example.student_service.model.Enrolment;
import com.example.student_service.model.Student;
import com.example.student_service.repository.CourseRepository;
import com.example.student_service.repository.EnrolmentRepository;
import com.example.student_service.repository.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrolmentRepository enrolmentRepository;

    @Mock
    private FinanceClient financeClient;

    @Mock
    private LibraryClient libraryClient;

    @InjectMocks
    private StudentService studentService;

    // Common test data
    private Student student;
    private Course course;
    private Enrolment enrolment;

    private static final Long STUDENT_ID = 1L;
    private static final Long COURSE_ID = 1L;
    private static final String USERNAME = "johndoe";
    private static final String EMAIL = "john@example.com";
    private static final String PASSWORD = "password123";
    private static final String STUDENT_NUMBER = "c1234567";

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(STUDENT_ID);
        student.setUsername(USERNAME);
        student.setEmail(EMAIL);
        student.setPassword(PASSWORD);
        student.setStudentId(STUDENT_NUMBER);

        course = new Course();
        course.setId(COURSE_ID);
        course.setCourseName("Computer Science");
        course.setPrice(1000.0);

        enrolment = new Enrolment();
        enrolment.setStudent(student);
        enrolment.setCourse(course);
    }

    // -------------------------
    // registerStudent
    // -------------------------

    @Test
    void registerStudent_Success() {
        when(studentRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(studentRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(studentRepository.save(org.mockito.ArgumentMatchers.any(Student.class))).thenReturn(student);

        Student result = studentService.registerStudent(USERNAME, EMAIL, PASSWORD);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        assertEquals(EMAIL, result.getEmail());
        verify(studentRepository).save(org.mockito.ArgumentMatchers.any(Student.class));
        verify(financeClient).createFianceAccount(result.getStudentId());
        verify(libraryClient).createLibraryAccount(result.getStudentId(), result.getUsername());
    }

    @Test
    void registerStudent_ThrowsException_WhenUsernameAlreadyExists() {
        when(studentRepository.existsByUsername(USERNAME)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.registerStudent(USERNAME, EMAIL, PASSWORD));

        assertEquals("Username is already in use", exception.getMessage());
        verify(studentRepository, never()).save(org.mockito.ArgumentMatchers.any(Student.class));
        verify(financeClient, never()).createFianceAccount(anyString());
        verify(libraryClient, never()).createLibraryAccount(anyString(), anyString());
    }

    @Test
    void registerStudent_ThrowsException_WhenEmailAlreadyExists() {
        when(studentRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(studentRepository.existsByEmail(EMAIL)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.registerStudent(USERNAME, EMAIL, PASSWORD));

        assertEquals("Email is already in use", exception.getMessage());
        verify(studentRepository, never()).save(org.mockito.ArgumentMatchers.any(Student.class));
        verify(financeClient, never()).createFianceAccount(anyString());
        verify(libraryClient, never()).createLibraryAccount(anyString(), anyString());
    }

    // -------------------------
    // updateProfile
    // -------------------------

    @Test
    void updateProfile_Success() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(studentRepository.save(org.mockito.ArgumentMatchers.any(Student.class))).thenReturn(student);

        Student result = studentService.updateProfile(STUDENT_ID, "John", "Doe", 21);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(21, result.getAge());
        verify(studentRepository).save(student);
    }

    @Test
    void updateProfile_ThrowsException_WhenStudentNotFound() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.updateProfile(STUDENT_ID, "John", "Doe", 21));

        assertEquals("Student not found", exception.getMessage());
        verify(studentRepository, never()).save(org.mockito.ArgumentMatchers.any(Student.class));
    }

    // -------------------------
    // getAllCourses
    // -------------------------

    @Test
    void getAllCourses_ReturnsListOfCourses() {
        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<Course> result = studentService.getAllCourses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(course, result.get(0));
    }

    @Test
    void getAllCourses_ReturnsEmptyList_WhenNoCoursesExist() {
        when(courseRepository.findAll()).thenReturn(List.of());

        List<Course> result = studentService.getAllCourses();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // -------------------------
    // enrolStudent
    // -------------------------

    @Test
    void enrolStudent_Success() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(enrolmentRepository.countByStudent(student)).thenReturn(Math.toIntExact((long) 2));
        when(enrolmentRepository.existsByStudentAndCourse(student, course)).thenReturn(false);
        when(enrolmentRepository.save(org.mockito.ArgumentMatchers.any(Enrolment.class))).thenReturn(enrolment);

        Enrolment result = studentService.enrolStudent(STUDENT_ID, COURSE_ID);

        assertNotNull(result);
        assertEquals(student, result.getStudent());
        assertEquals(course, result.getCourse());
        verify(enrolmentRepository).save(org.mockito.ArgumentMatchers.any(Enrolment.class));
        verify(financeClient).addCourseFee(eq(student.getStudentId()), eq(course.getPrice()), anyString());
    }

    @Test
    void enrolStudent_ThrowsException_WhenStudentNotFound() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.enrolStudent(STUDENT_ID, COURSE_ID));

        assertEquals("Student not found", exception.getMessage());
        verify(enrolmentRepository, never()).save(org.mockito.ArgumentMatchers.any(Enrolment.class));
    }

    @Test
    void enrolStudent_ThrowsException_WhenCourseNotFound() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.enrolStudent(STUDENT_ID, COURSE_ID));

        assertEquals("Course not found", exception.getMessage());
        verify(enrolmentRepository, never()).save(org.mockito.ArgumentMatchers.any(Enrolment.class));
    }

    @Test
    void enrolStudent_ThrowsException_WhenMaxEnrolmentsReached() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(enrolmentRepository.countByStudent(student)).thenReturn(Math.toIntExact((long) 3));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.enrolStudent(STUDENT_ID, COURSE_ID));

        assertEquals("You are already enrolled on the maximum of 3 courses", exception.getMessage());
        verify(enrolmentRepository, never()).save(org.mockito.ArgumentMatchers.any(Enrolment.class));
    }

    @Test
    void enrolStudent_ThrowsException_WhenAlreadyEnrolledOnCourse() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
        when(enrolmentRepository.countByStudent(student)).thenReturn(Math.toIntExact((long) 2));
        when(enrolmentRepository.existsByStudentAndCourse(student, course)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.enrolStudent(STUDENT_ID, COURSE_ID));

        assertEquals("You are already enrolled on this course", exception.getMessage());
        verify(enrolmentRepository, never()).save(org.mockito.ArgumentMatchers.any(Enrolment.class));
    }

    // -------------------------
    // getEnrolmentsForStudent
    // -------------------------

    @Test
    void getEnrolmentsForStudent_Success() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(enrolmentRepository.findByStudent(student)).thenReturn(List.of(enrolment));

        List<Enrolment> result = studentService.getEnrolmentsForStudent(STUDENT_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(enrolment, result.get(0));
    }

    @Test
    void getEnrolmentsForStudent_ThrowsException_WhenStudentNotFound() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.getEnrolmentsForStudent(STUDENT_ID));

        assertEquals("Student not found", exception.getMessage());
    }

    // -------------------------
    // canGraduate
    // -------------------------

    @Test
    void canGraduate_ReturnsTrue_WhenEnrolledAndBillPaid() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(enrolmentRepository.findByStudent(student)).thenReturn(List.of(enrolment));
        when(financeClient.isBillPaid(student.getStudentId())).thenReturn(true);

        boolean result = studentService.canGraduate(STUDENT_ID);

        assertTrue(result);
    }

    @Test
    void canGraduate_ReturnsFalse_WhenEnrolledAndBillNotPaid() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(enrolmentRepository.findByStudent(student)).thenReturn(List.of(enrolment));
        when(financeClient.isBillPaid(student.getStudentId())).thenReturn(false);

        boolean result = studentService.canGraduate(STUDENT_ID);

        assertFalse(result);
    }

    @Test
    void canGraduate_ReturnsFalse_WhenNotEnrolledOnAnyCourse() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(enrolmentRepository.findByStudent(student)).thenReturn(List.of());

        boolean result = studentService.canGraduate(STUDENT_ID);

        assertFalse(result);
        verify(financeClient, never()).isBillPaid(anyString());
    }

    @Test
    void canGraduate_ThrowsException_WhenStudentNotFound() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.canGraduate(STUDENT_ID));

        assertEquals("Student not found", exception.getMessage());
    }

    // -------------------------
    // getStudentById
    // -------------------------

    @Test
    void getStudentById_Success() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));

        Student result = studentService.getStudentById(STUDENT_ID);

        assertNotNull(result);
        assertEquals(student, result);
    }

    @Test
    void getStudentById_ThrowsException_WhenStudentNotFound() {
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.getStudentById(STUDENT_ID));

        assertEquals("Student not found", exception.getMessage());
    }

    // -------------------------
    // getAllStudents
    // -------------------------

    @Test
    void getAllStudents_ReturnsListOfStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(student));

        List<Student> result = studentService.getAllStudents();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(student, result.get(0));
    }

    @Test
    void getAllStudents_ReturnsEmptyList_WhenNoStudentsExist() {
        when(studentRepository.findAll()).thenReturn(List.of());

        List<Student> result = studentService.getAllStudents();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // -------------------------
    // loginStudent
    // -------------------------

    @Test
    void loginStudent_Success() {
        when(studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(student));

        Student result = studentService.loginStudent(USERNAME, PASSWORD);

        assertNotNull(result);
        assertEquals(student, result);
    }

    @Test
    void loginStudent_ThrowsException_WhenUsernameNotFound() {
        when(studentRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.loginStudent(USERNAME, PASSWORD));

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void loginStudent_ThrowsException_WhenPasswordIsIncorrect() {
        when(studentRepository.findByUsername(USERNAME)).thenReturn(Optional.of(student));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> studentService.loginStudent(USERNAME, "wrongpassword"));

        assertEquals("Invalid username or password", exception.getMessage());
    }
}