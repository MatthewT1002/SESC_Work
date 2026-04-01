package com.example.student_service.service;

import com.example.student_service.InvoiceGenerator;
import com.example.student_service.client.FinanceClient;
import com.example.student_service.client.LibraryClient;
import com.example.student_service.model.Student;
import com.example.student_service.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.student_service.client.FinanceClient;
import com.example.student_service.model.Course;
import com.example.student_service.model.Enrolment;
import com.example.student_service.repository.CourseRepository;
import com.example.student_service.repository.EnrolmentRepository;
import java.util.Random;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrolmentRepository enrolmentRepository;

    @Autowired
    private FinanceClient financeClient;

    @Autowired
    private LibraryClient libraryClient;

    public Student registerStudent(String username, String email, String password) {

        if (studentRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already in use");
        }

        if (studentRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use");
        }

        Student student = new Student();
        student.setUsername(username);
        student.setEmail(email);
        student.setPassword(password);
        student.setStudentId(generateStudentId());

       Student savedStudent = studentRepository.save(student);
       financeClient.createFianceAccount(savedStudent.getStudentId());
       libraryClient.createLibraryAccount(savedStudent.getStudentId(), savedStudent.getUsername());
       return savedStudent;
    }

    public Student updateProfile(Long id, String firstName, String lastName, Integer age) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setAge(age);

        return studentRepository.save(student);
    }

    private String generateStudentId() {
        Random random = new Random();
        int idNo = 1000000 + random.nextInt(9000000);
        return "c" + idNo;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

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

    public List<Enrolment> getEnrolmentsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return enrolmentRepository.findByStudent(student);
    }

    public boolean canGraduate(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Enrolment> enrolments = enrolmentRepository.findByStudent(student);
        if (enrolments.isEmpty()) {
            return false;
        }

        return financeClient.isBillPaid(student.getStudentId());
    }

    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student loginStudent(String username, String password) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!student.getPassword().equals(password)) {
            throw new RuntimeException("Invalid username or password");
        }

        return student;
    }
}
