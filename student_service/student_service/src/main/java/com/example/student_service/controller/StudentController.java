package com.example.student_service.controller;

import com.example.student_service.model.Enrolment;
import com.example.student_service.model.Student;
import com.example.student_service.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerStudent(@RequestParam String username,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  Model model) {
        try {
            Student student = studentService.registerStudent(username, email, password);
            model.addAttribute("student", student);
            return "redirect:/home/" + student.getId();
        }

        catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/profile/{id}")
    public String showProfilePage(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        model.addAttribute("id", id);
        return "profile";
    }

    @PostMapping("/profile/{id}")
    public String updateProfile(@PathVariable Long id,
                                @RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam Integer age,
                                Model model) {
        try {
            Student student = studentService.updateProfile(id, firstName, lastName, age);
            model.addAttribute("student", student);
            model.addAttribute("id", id);
            model.addAttribute("success", "Profile updated successfully!");
            return "profile";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("id", id);
            return "profile";
        }
    }

    @GetMapping("/courses/{id}")
    public String showCoursesPage(@PathVariable Long id, Model model) {
        model.addAttribute("courses", studentService.getAllCourses());
        model.addAttribute("id", id);
        return "courses";
    }

    @PostMapping("/enrol/{studentId}/{courseId}")
    public String enrolStudent(@PathVariable Long studentId,
                               @PathVariable Long courseId,
                               Model model) {
        try {
            studentService.enrolStudent(studentId, courseId);
            return "redirect:/enrolments/" + studentId;
        }
        catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("courses", studentService.getAllCourses());
            model.addAttribute("id", studentId);
            return "courses";
        }
    }

    @GetMapping("/enrolments/{id}")
    public String showEnrolmentPage(@PathVariable Long id, Model model) {
        model.addAttribute("enrolments", studentService.getEnrolmentsForStudent(id));
        model.addAttribute("id", id);
        return "enrolments";
    }

    @GetMapping("/graduation/{id}")
    public String showGraduationPage(@PathVariable Long id, Model model) {
        List<Enrolment> enrolments = studentService.getEnrolmentsForStudent(id);
        boolean canGraduate = studentService.canGraduate(id);
        model.addAttribute("enrolments", enrolments);
        model.addAttribute("id", id);
        model.addAttribute("billsPaid", canGraduate);
        return "graduation";
    }

    @GetMapping("/home/{id}")
    public String showHomePage(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return "home";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginStudent(@RequestParam String username,
                               @RequestParam String password,
                               Model model) {
        try {
            Student student = studentService.loginStudent(username, password);
            return "redirect:/home/" + student.getId();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
}
