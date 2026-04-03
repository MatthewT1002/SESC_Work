package com.example.student_service;

import com.example.student_service.model.Course;
import com.example.student_service.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataLoaderTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private DataLoader dataLoader;

    // -------------------------
    // run
    // -------------------------

    @Test
    void run_LoadsCourses_WhenRepositoryIsEmpty() throws Exception {
        when(courseRepository.count()).thenReturn(0L);

        dataLoader.run();

        verify(courseRepository, times(50)).save(org.mockito.ArgumentMatchers.any(Course.class));
    }

    @Test
    void run_DoesNotLoadCourses_WhenRepositoryIsNotEmpty() throws Exception {
        when(courseRepository.count()).thenReturn(10L);

        dataLoader.run();

        verify(courseRepository, never()).save(org.mockito.ArgumentMatchers.any(Course.class));
    }

    @Test
    void run_SavesCoursesWithCorrectFields() throws Exception {
        when(courseRepository.count()).thenReturn(0L);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        dataLoader.run();
        verify(courseRepository, times(50)).save(captor.capture());

        List<Course> savedCourses = captor.getAllValues();

        // Verify the first course
        Course first = savedCourses.get(0);
        assertEquals("CS101", first.getCourseCode());
        assertEquals("Introduction to Programming", first.getCourseName());
        assertEquals("Learn the basics of programming", first.getCourseDescription());
        assertEquals(1200.00, first.getPrice());
    }

    @Test
    void run_SavesCoursesWithUniqueCourseCodes() throws Exception {
        when(courseRepository.count()).thenReturn(0L);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        dataLoader.run();
        verify(courseRepository, times(50)).save(captor.capture());

        List<Course> savedCourses = captor.getAllValues();
        long uniqueCodes = savedCourses.stream()
                .map(Course::getCourseCode)
                .distinct()
                .count();

        assertEquals(50, uniqueCodes);
    }

    @Test
    void run_SavesCoursesWithUniqueNames() throws Exception {
        when(courseRepository.count()).thenReturn(0L);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        dataLoader.run();
        verify(courseRepository, times(50)).save(captor.capture());

        List<Course> savedCourses = captor.getAllValues();
        long uniqueNames = savedCourses.stream()
                .map(Course::getCourseName)
                .distinct()
                .count();

        assertEquals(50, uniqueNames);
    }

    @Test
    void run_SavesCoursesWithValidPrices() throws Exception {
        when(courseRepository.count()).thenReturn(0L);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        dataLoader.run();
        verify(courseRepository, times(50)).save(captor.capture());

        List<Course> savedCourses = captor.getAllValues();
        savedCourses.forEach(course ->
                assertTrue(course.getPrice() > 0, "Price should be greater than 0 for: " + course.getCourseCode())
        );
    }

    @Test
    void run_SavesCoursesWithNonEmptyFields() throws Exception {
        when(courseRepository.count()).thenReturn(0L);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        dataLoader.run();
        verify(courseRepository, times(50)).save(captor.capture());

        List<Course> savedCourses = captor.getAllValues();
        savedCourses.forEach(course -> {
            assertNotNull(course.getCourseCode());
            assertNotNull(course.getCourseName());
            assertNotNull(course.getCourseDescription());
            assertNotNull(course.getPrice());
            assertFalse(course.getCourseCode().isEmpty());
            assertFalse(course.getCourseName().isEmpty());
            assertFalse(course.getCourseDescription().isEmpty());
        });
    }

    @Test
    void run_SavesLastCourseCorrectly() throws Exception {
        when(courseRepository.count()).thenReturn(0L);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        dataLoader.run();
        verify(courseRepository, times(50)).save(captor.capture());

        List<Course> savedCourses = captor.getAllValues();

        // Verify the last course
        Course last = savedCourses.get(49);
        assertEquals("SO101", last.getCourseCode());
        assertEquals("Sociology", last.getCourseName());
        assertEquals("The study of society, culture and social behaviour", last.getCourseDescription());
        assertEquals(1000.00, last.getPrice());
    }
}