package com.example.student_service;

import com.example.student_service.model.Course;
import com.example.student_service.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void run(String... args) {
        if (courseRepository.count() == 0) {
            addCourse("CS101", "Introduction to Programming", "Learn the basics of programming", 1200.00);
            addCourse("CS102", "Data Structures", "Arrays, lists, trees and more", 1200.00);
            addCourse("CS103", "Databases", "SQL and relational databases", 1200.00);
            addCourse("CS104", "Networking", "How the internet works", 1200.00);
            addCourse("CS105", "Software Engineering", "Building real world software", 1200.00);
            addCourse("CS106", "Artificial Intelligence", "Introduction to AI and machine learning", 1500.00);
            addCourse("CS107", "Cybersecurity", "Fundamentals of securing systems and networks", 1500.00);
            addCourse("CS108", "Cloud Computing", "Working with cloud platforms and services", 1300.00);
            addCourse("CS109", "Mobile App Development", "Building apps for iOS and Android", 1300.00);
            addCourse("CS110", "Web Development", "HTML, CSS, JavaScript and modern frameworks", 1200.00);
            addCourse("MA101", "Calculus", "Differential and integral calculus", 1100.00);
            addCourse("MA102", "Linear Algebra", "Vectors, matrices and linear transformations", 1100.00);
            addCourse("MA103", "Statistics", "Probability and statistical analysis", 1100.00);
            addCourse("MA104", "Discrete Mathematics", "Logic, sets, graphs and combinatorics", 1100.00);
            addCourse("MA105", "Numerical Methods", "Computational approaches to mathematical problems", 1100.00);
            addCourse("MA106", "Differential Equations", "Ordinary and partial differential equations", 1100.00);
            addCourse("MA107", "Mathematical Modelling", "Applying mathematics to real world problems", 1100.00);
            addCourse("PH101", "Classical Mechanics", "Newtons laws, motion and energy", 1100.00);
            addCourse("PH102", "Electromagnetism", "Electric and magnetic fields and their interactions", 1100.00);
            addCourse("PH103", "Quantum Mechanics", "Introduction to quantum theory and wave functions", 1200.00);
            addCourse("PH104", "Thermodynamics", "Heat, energy and the laws of thermodynamics", 1100.00);
            addCourse("PH105", "Astrophysics", "The physics of stars, galaxies and the universe", 1200.00);
            addCourse("EN101", "English Literature", "Analysis of classic and contemporary literature", 900.00);
            addCourse("EN102", "Creative Writing", "Techniques for fiction and non-fiction writing", 900.00);
            addCourse("EN103", "Academic Writing", "Writing essays and reports to a high standard", 900.00);
            addCourse("EN104", "Linguistics", "The structure and evolution of language", 900.00);
            addCourse("BS101", "Business Management", "Principles of managing organisations", 1200.00);
            addCourse("BS102", "Marketing", "Consumer behaviour and marketing strategy", 1200.00);
            addCourse("BS103", "Accounting", "Financial reporting and bookkeeping", 1200.00);
            addCourse("BS104", "Economics", "Micro and macroeconomic theory", 1200.00);
            addCourse("BS105", "Entrepreneurship", "Starting and running a business", 1200.00);
            addCourse("BS106", "Human Resource Management", "Managing people in organisations", 1100.00);
            addCourse("PS101", "Introduction to Psychology", "Fundamentals of human behaviour and the mind", 1000.00);
            addCourse("PS102", "Cognitive Psychology", "How people think, learn and remember", 1000.00);
            addCourse("PS103", "Social Psychology", "How people influence and relate to each other", 1000.00);
            addCourse("PS104", "Developmental Psychology", "Human development across the lifespan", 1000.00);
            addCourse("LA101", "Contract Law", "Formation and enforcement of contracts", 1300.00);
            addCourse("LA102", "Criminal Law", "Principles of criminal liability and procedure", 1300.00);
            addCourse("LA103", "Constitutional Law", "The structure and principles of constitutional systems", 1300.00);
            addCourse("EG101", "Electrical Engineering", "Circuits, signals and electrical systems", 1400.00);
            addCourse("EG102", "Mechanical Engineering", "Forces, materials and mechanical systems", 1400.00);
            addCourse("EG103", "Civil Engineering", "Design and construction of infrastructure", 1400.00);
            addCourse("EG104", "Chemical Engineering", "Chemical processes and reactor design", 1400.00);
            addCourse("HI101", "Ancient History", "Civilisations of the ancient world", 900.00);
            addCourse("HI102", "Modern History", "World events from the 18th century to present", 900.00);
            addCourse("HI103", "History of Science", "How scientific knowledge has developed over time", 900.00);
            addCourse("AR101", "Fine Art", "Drawing, painting and visual composition", 950.00);
            addCourse("AR102", "Graphic Design", "Visual communication and digital design", 1000.00);
            addCourse("MU101", "Music Theory", "The fundamentals of musical structure and composition", 950.00);
            addCourse("SO101", "Sociology", "The study of society, culture and social behaviour", 1000.00);
        }
    }

    private void addCourse(String code, String name, String description, Double price) {
        Course c = new Course();
        c.setCourseCode(code);
        c.setCourseName(name);
        c.setCourseDescription(description);
        c.setPrice(price);
        courseRepository.save(c);
    }
}
