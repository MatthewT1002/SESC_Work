package com.library_service.library_service.controller;

import com.library_service.library_service.model.LibraryAccount;
import com.library_service.library_service.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryRestControllerTest {

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private LibraryRestController libraryRestController;

    private LibraryAccount account;

    private static final String STUDENT_ID = "c1234567";
    private static final String USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        account = new LibraryAccount();
        account.setStudentId(STUDENT_ID);
        account.setUsername(USERNAME);
    }

    // -------------------------
    // POST /api/library/account
    // -------------------------

    @Test
    void createAccount_Returns200_WhenAccountCreatedSuccessfully() {
        when(libraryService.createAccount(STUDENT_ID, USERNAME)).thenReturn(account);

        ResponseEntity<LibraryAccount> response = libraryRestController.createAccount(STUDENT_ID, USERNAME);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(STUDENT_ID, response.getBody().getStudentId());
        assertEquals(USERNAME, response.getBody().getUsername());
        verify(libraryService).createAccount(STUDENT_ID, USERNAME);
    }

    @Test
    void createAccount_Returns400_WhenAccountAlreadyExists() {
        when(libraryService.createAccount(STUDENT_ID, USERNAME))
                .thenThrow(new RuntimeException("Account already exists for this student"));

        ResponseEntity<LibraryAccount> response = libraryRestController.createAccount(STUDENT_ID, USERNAME);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // -------------------------
    // GET /api/library/account/{studentId}
    // -------------------------

    @Test
    void getAccount_Returns200_WhenAccountFound() {
        when(libraryService.getAccount(STUDENT_ID)).thenReturn(account);

        ResponseEntity<LibraryAccount> response = libraryRestController.getAccount(STUDENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(STUDENT_ID, response.getBody().getStudentId());
        assertEquals(USERNAME, response.getBody().getUsername());
        verify(libraryService).getAccount(STUDENT_ID);
    }

    @Test
    void getAccount_Returns404_WhenAccountNotFound() {
        when(libraryService.getAccount(STUDENT_ID))
                .thenThrow(new RuntimeException("Library account not found"));

        ResponseEntity<LibraryAccount> response = libraryRestController.getAccount(STUDENT_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}