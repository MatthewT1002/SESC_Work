package com.example.student_service.service;

import com.example.student_service.InvoiceGenerator;
import com.example.student_service.client.FinanceClient;
import com.example.student_service.client.LibraryClient;
import com.example.student_service.model.Student;
import com.example.student_service.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.student_service.model.Course;
import com.example.student_service.model.Enrolment;
import com.example.student_service.repository.CourseRepository;
import com.example.student_service.repository.EnrolmentRepository;
import java.util.Random;
import java.util.List;

/**
 * Service layer for all student related business logic.
 */

@Service // Registers this as a Spring Bean and signals it contains business logic.
public class StudentService {

    // All dependencies are automatically injected.

    @Autowired
    private StudentRepository studentRepository; // Database access for student.

    @Autowired
    private CourseRepository courseRepository; // Database access for courses.

    @Autowired
    private EnrolmentRepository enrolmentRepository; // Database access for enrolment.

    @Autowired
    private FinanceClient financeClient; // HTTP client for the Finance Service.

    @Autowired
    private LibraryClient libraryClient; // HTTP client for the Library Service.

    /**
     * Registers a new student, persists them to the database, and creates corresponding
     * accounts in the Finance and Library services.
     *
     * @param username set username.
     * @param email set email address.
     * @param password the students password.
     * @return if username and email is already in use.
     */

    public Student registerStudent(String username, String email, String password) {

        // Validate username and email uniqueness.
        if (studentRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already in use");
        }

        if (studentRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use");
        }

        // Build new student object.
        Student student = new Student();
        student.setUsername(username);
        student.setEmail(email);
        student.setPassword(password);
        student.setStudentId(generateStudentId());

        // Persists to database first so a valid student ID is accessible for other services.
       Student savedStudent = studentRepository.save(student);

       // Notify Finance and Library services to create corresponding accounts.
       financeClient.createFianceAccount(savedStudent.getStudentId());
       libraryClient.createLibraryAccount(savedStudent.getStudentId(), savedStudent.getUsername());
       return savedStudent;
    }

    /**
     * Updates the profiles details for existing student.
     * @param id
     * @param firstName
     * @param lastName
     * @param age
     * @return
     */
    public Student updateProfile(Long id, String firstName, String lastName, Integer age) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setAge(age);

        return studentRepository.save(student);
    }

    /**
     * Generates unique student id for human use. Uses random number between 1000000 and 9000000,
     * and "c" at the start.
     * @return
     */
    private String generateStudentId() {
        Random random = new Random();
        int idNo = 1000000 + random.nextInt(9000000); // Always 7 digits.
        return "c" + idNo;
    }

    // Retrieves all courses available for enrolment.
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    /**
     * Enrols student upon course, genearates invoice, persists the enrolment and
     * notifies the Finance Service of the charge.
     * @param studentId
     * @param courseId
     * @return
     */
    public Enrolment enrolStudent (Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (enrolmentRepository.countByStudent(student) >= 3) {
            throw new RuntimeException("You are already enrolled on the maximum of 3 courses");
        }

        if (enrolmentRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("You are already enrolled on this course");
        }

        Enrolment enrolment = new Enrolment();
        enrolment.setStudent(student);
        enrolment.setCourse(course);

        String invoiceNumber = InvoiceGenerator.generate();
        enrolment.setInvoiceNumber(invoiceNumber);
        Enrolment savedEnrolment = enrolmentRepository.save(enrolment);
        financeClient.addCourseFee(student.getStudentId(), course.getPrice(), invoiceNumber);
        return savedEnrolment;
    }

    /**
     * Retrieves all enrolments for a given studnet.
     * @param studentId
     * @return
     */
    public List<Enrolment> getEnrolmentsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return enrolmentRepository.findByStudent(student);
    }

    /**
     * Determines if a student can graduate if their finance is paid, and they are enrolled
     * on a course.
     * @param studentId
     * @return
     */
    public boolean canGraduate(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Enrolment> enrolments = enrolmentRepository.findByStudent(student);
        if (enrolments.isEmpty()) {
            return false;
        }

        return financeClient.isBillPaid(student.getStudentId());
    }

    /**
     * returns student by id.
     * @param studentId
     * @return
     */
    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    /**
     * Retrieves all students within the system.
     * @return
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Validates login credentials and returns matching student.
     * @param username
     * @param password
     * @return
     */
    public Student loginStudent(String username, String password) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!student.getPassword().equals(password)) {
            throw new RuntimeException("Invalid username or password");
        }

        return student;
    }
}
