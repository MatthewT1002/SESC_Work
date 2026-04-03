package com.example.student_service.controller;

import com.example.student_service.model.Course;
import com.example.student_service.model.Enrolment;
import com.example.student_service.model.Student;
import com.example.student_service.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @Mock
    private Model model;

    @InjectMocks
    private StudentController studentController;

    private Student student;
    private Course course;
    private Enrolment enrolment;

    private static final Long STUDENT_ID = 1L;
    private static final Long COURSE_ID = 1L;
    private static final String USERNAME = "testuser";
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(STUDENT_ID);
        student.setUsername(USERNAME);
        student.setEmail(EMAIL);
        student.setPassword(PASSWORD);

        course = new Course();
        course.setId(COURSE_ID);

        enrolment = new Enrolment();
        enrolment.setStudent(student);
        enrolment.setCourse(course);
    }

    // -------------------------
    // GET /register
    // -------------------------

    @Test
    void showRegisterPage_ReturnsRegisterView() {
        String view = studentController.showRegisterPage();
        assertEquals("register", view);
    }

    // -------------------------
    // POST /register
    // -------------------------

    @Test
    void registerStudent_Success_RedirectsToHome() {
        when(studentService.registerStudent(USERNAME, EMAIL, PASSWORD)).thenReturn(student);

        String view = studentController.registerStudent(USERNAME, EMAIL, PASSWORD, model);

        assertEquals("redirect:/home/" + STUDENT_ID, view);
        verify(model).addAttribute("student", student);
    }

    @Test
    void registerStudent_ReturnsRegisterView_WhenExceptionThrown() {
        when(studentService.registerStudent(USERNAME, EMAIL, PASSWORD))
                .thenThrow(new RuntimeException("Username is already in use"));

        String view = studentController.registerStudent(USERNAME, EMAIL, PASSWORD, model);

        assertEquals("register", view);
        verify(model).addAttribute("error", "Username is already in use");
    }

    // -------------------------
    // GET /profile/{id}
    // -------------------------

    @Test
    void showProfilePage_ReturnsProfileView() {
        when(studentService.getStudentById(STUDENT_ID)).thenReturn(student);

        String view = studentController.showProfilePage(STUDENT_ID, model);

        assertEquals("profile", view);
        verify(model).addAttribute("student", student);
        verify(model).addAttribute("id", STUDENT_ID);
    }

    // -------------------------
    // POST /profile/{id}
    // -------------------------

    @Test
    void updateProfile_Success_ReturnsProfileView() {
        when(studentService.updateProfile(STUDENT_ID, "John", "Doe", 21)).thenReturn(student);

        String view = studentController.updateProfile(STUDENT_ID, "John", "Doe", 21, model);

        assertEquals("profile", view);
        verify(model).addAttribute("student", student);
        verify(model).addAttribute("id", STUDENT_ID);
        verify(model).addAttribute("success", "Profile updated successfully!");
    }

    @Test
    void updateProfile_ReturnsProfileView_WhenExceptionThrown() {
        when(studentService.updateProfile(STUDENT_ID, "John", "Doe", 21))
                .thenThrow(new RuntimeException("Student not found"));

        String view = studentController.updateProfile(STUDENT_ID, "John", "Doe", 21, model);

        assertEquals("profile", view);
        verify(model).addAttribute("error", "Student not found");
        verify(model).addAttribute("id", STUDENT_ID);
    }

    // -------------------------
    // GET /courses/{id}
    // -------------------------

    @Test
    void showCoursesPage_ReturnsCoursesView() {
        when(studentService.getAllCourses()).thenReturn(List.of(course));

        String view = studentController.showCoursesPage(STUDENT_ID, model);

        assertEquals("courses", view);
        verify(model).addAttribute("courses", List.of(course));
        verify(model).addAttribute("id", STUDENT_ID);
    }

    // -------------------------
    // POST /enrol/{studentId}/{courseId}
    // -------------------------

    @Test
    void enrolStudent_Success_RedirectsToEnrolments() {
        when(studentService.enrolStudent(STUDENT_ID, COURSE_ID)).thenReturn(enrolment);

        String view = studentController.enrolStudent(STUDENT_ID, COURSE_ID, model);

        assertEquals("redirect:/enrolments/" + STUDENT_ID, view);
    }

    @Test
    void enrolStudent_ReturnsCoursesView_WhenExceptionThrown() {
        when(studentService.enrolStudent(STUDENT_ID, COURSE_ID))
                .thenThrow(new RuntimeException("You are already enrolled on this course"));
        when(studentService.getAllCourses()).thenReturn(List.of(course));

        String view = studentController.enrolStudent(STUDENT_ID, COURSE_ID, model);

        assertEquals("courses", view);
        verify(model).addAttribute("error", "You are already enrolled on this course");
        verify(model).addAttribute("courses", List.of(course));
        verify(model).addAttribute("id", STUDENT_ID);
    }

    // -------------------------
    // GET /enrolments/{id}
    // -------------------------

    @Test
    void showEnrolmentPage_ReturnsEnrolmentsView() {
        when(studentService.getEnrolmentsForStudent(STUDENT_ID)).thenReturn(List.of(enrolment));

        String view = studentController.showEnrolmentPage(STUDENT_ID, model);

        assertEquals("enrolments", view);
        verify(model).addAttribute("enrolments", List.of(enrolment));
        verify(model).addAttribute("id", STUDENT_ID);
    }

    // -------------------------
    // GET /graduation/{id}
    // -------------------------

    @Test
    void showGraduationPage_ReturnsGraduationView_WhenCanGraduate() {
        when(studentService.getEnrolmentsForStudent(STUDENT_ID)).thenReturn(List.of(enrolment));
        when(studentService.canGraduate(STUDENT_ID)).thenReturn(true);

        String view = studentController.showGraduationPage(STUDENT_ID, model);

        assertEquals("graduation", view);
        verify(model).addAttribute("enrolments", List.of(enrolment));
        verify(model).addAttribute("id", STUDENT_ID);
        verify(model).addAttribute("billsPaid", true);
    }

    @Test
    void showGraduationPage_ReturnsGraduationView_WhenCannotGraduate() {
        when(studentService.getEnrolmentsForStudent(STUDENT_ID)).thenReturn(List.of(enrolment));
        when(studentService.canGraduate(STUDENT_ID)).thenReturn(false);

        String view = studentController.showGraduationPage(STUDENT_ID, model);

        assertEquals("graduation", view);
        verify(model).addAttribute("billsPaid", false);
    }

    // -------------------------
    // GET /home/{id}
    // -------------------------

    @Test
    void showHomePage_ReturnsHomeView() {
        String view = studentController.showHomePage(STUDENT_ID, model);

        assertEquals("home", view);
        verify(model).addAttribute("id", STUDENT_ID);
    }

    // -------------------------
    // GET /login
    // -------------------------

    @Test
    void showLoginPage_ReturnsLoginView() {
        String view = studentController.showLoginPage();
        assertEquals("login", view);
    }

    // -------------------------
    // POST /login
    // -------------------------

    @Test
    void loginStudent_Success_RedirectsToHome() {
        when(studentService.loginStudent(USERNAME, PASSWORD)).thenReturn(student);

        String view = studentController.loginStudent(USERNAME, PASSWORD, model);

        assertEquals("redirect:/home/" + STUDENT_ID, view);
    }

    @Test
    void loginStudent_ReturnsLoginView_WhenExceptionThrown() {
        when(studentService.loginStudent(USERNAME, PASSWORD))
                .thenThrow(new RuntimeException("Invalid username or password"));

        String view = studentController.loginStudent(USERNAME, PASSWORD, model);

        assertEquals("login", view);
        verify(model).addAttribute("error", "Invalid username or password");
    }

    // -------------------------
    // GET /
    // -------------------------

    @Test
    void index_RedirectsToLogin() {
        String view = studentController.index();
        assertEquals("redirect:/login", view);
    }
}