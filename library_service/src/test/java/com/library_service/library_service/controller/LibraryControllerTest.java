package com.library_service.library_service.controller;

import com.library_service.library_service.model.BorrowedBook;
import com.library_service.library_service.model.LibraryAccount;
import com.library_service.library_service.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryControllerTest {

    @Mock
    private LibraryService libraryService;

    @Mock
    private Model model;

    @InjectMocks
    private LibraryController libraryController;

    private LibraryAccount account;
    private BorrowedBook borrowedBook;

    private static final String STUDENT_ID = "c1234567";
    private static final String USERNAME = "testuser";
    private static final String ISBN = "978-3-16-148410-0";
    private static final String TITLE = "Effective Java";
    private static final String AUTHOR = "Joshua Bloch";
    private static final Long BOOK_ID = 1L;

    @BeforeEach
    void setUp() {
        account = new LibraryAccount();
        account.setStudentId(STUDENT_ID);
        account.setUsername(USERNAME);

        borrowedBook = new BorrowedBook();
        borrowedBook.setLibraryAccount(account);
        borrowedBook.setIsbn(ISBN);
        borrowedBook.setTitle(TITLE);
        borrowedBook.setAuthor(AUTHOR);
    }

    // -------------------------
    // GET /
    // -------------------------

    @Test
    void index_RedirectsToLogin() {
        String view = libraryController.index();
        assertEquals("redirect:/login", view);
    }

    // -------------------------
    // GET /login
    // -------------------------

    @Test
    void showLoginPage_ReturnsLoginView() {
        String view = libraryController.showLoginPage();
        assertEquals("login", view);
    }

    // -------------------------
    // POST /login
    // -------------------------

    @Test
    void login_Success_RedirectsToLibrary() {
        when(libraryService.getAccount(STUDENT_ID)).thenReturn(account);

        String view = libraryController.login(STUDENT_ID, USERNAME, model);

        assertEquals("redirect:/library/" + STUDENT_ID, view);
    }

    @Test
    void login_ReturnsLoginView_WhenAccountNotFound() {
        when(libraryService.getAccount(STUDENT_ID))
                .thenThrow(new RuntimeException("Library account not found"));

        String view = libraryController.login(STUDENT_ID, USERNAME, model);

        assertEquals("login", view);
        verify(model).addAttribute("error", "Account not found. Please contact student services.");
    }

    // -------------------------
    // GET /library/{studentId}
    // -------------------------

    @Test
    void showLibrary_ReturnsLibraryView_WithNoQuery() {
        when(libraryService.getAccount(STUDENT_ID)).thenReturn(account);

        String view = libraryController.showLibrary(STUDENT_ID, null, model);

        assertEquals("library", view);
        verify(model).addAttribute("account", account);
        verify(model).addAttribute("studentId", STUDENT_ID);
        verify(libraryService, never()).searchBooks(anyString());
    }

    @Test
    void showLibrary_ReturnsLibraryView_WithEmptyQuery() {
        when(libraryService.getAccount(STUDENT_ID)).thenReturn(account);

        String view = libraryController.showLibrary(STUDENT_ID, "", model);

        assertEquals("library", view);
        verify(libraryService, never()).searchBooks(anyString());
    }

    @Test
    void showLibrary_ReturnsLibraryView_WithSearchResults() {
        List<Map<String, String>> books = List.of(Map.of("title", TITLE, "author", AUTHOR));
        when(libraryService.getAccount(STUDENT_ID)).thenReturn(account);
        when(libraryService.searchBooks("Effective Java")).thenReturn(books);

        String view = libraryController.showLibrary(STUDENT_ID, "Effective Java", model);

        assertEquals("library", view);
        verify(model).addAttribute("books", books);
        verify(model).addAttribute("query", "Effective Java");
    }

    @Test
    void showLibrary_ReturnsLoginView_WhenAccountNotFound() {
        when(libraryService.getAccount(STUDENT_ID))
                .thenThrow(new RuntimeException("Library account not found"));

        String view = libraryController.showLibrary(STUDENT_ID, null, model);

        assertEquals("login", view);
        verify(model).addAttribute("error", "Library account not found");
    }

    // -------------------------
    // POST /library/{studentId}/borrow
    // -------------------------

    @Test
    void borrowBook_Success_RedirectsToBorrowed() {
        when(libraryService.borrowBook(STUDENT_ID, ISBN, TITLE, AUTHOR)).thenReturn(borrowedBook);

        String view = libraryController.borrowBook(STUDENT_ID, ISBN, TITLE, AUTHOR, model);

        assertEquals("redirect:/library/" + STUDENT_ID + "/borrowed", view);
    }

    @Test
    void borrowBook_ReturnsLibraryView_WhenExceptionThrown() {
        when(libraryService.borrowBook(STUDENT_ID, ISBN, TITLE, AUTHOR))
                .thenThrow(new RuntimeException("You have already borrowed this book"));

        String view = libraryController.borrowBook(STUDENT_ID, ISBN, TITLE, AUTHOR, model);

        assertEquals("library", view);
        verify(model).addAttribute("error", "You have already borrowed this book");
        verify(model).addAttribute("studentId", STUDENT_ID);
    }

    @Test
    void borrowBook_ReturnsLibraryView_WhenMaxBooksReached() {
        when(libraryService.borrowBook(STUDENT_ID, ISBN, TITLE, AUTHOR))
                .thenThrow(new RuntimeException("You have reached the maximum of 10 borrowed books"));

        String view = libraryController.borrowBook(STUDENT_ID, ISBN, TITLE, AUTHOR, model);

        assertEquals("library", view);
        verify(model).addAttribute("error", "You have reached the maximum of 10 borrowed books");
        verify(model).addAttribute("studentId", STUDENT_ID);
    }

    // -------------------------
    // GET /library/{studentId}/borrowed
    // -------------------------

    @Test
    void showBorrowedBooks_ReturnsBorrowedView() {
        when(libraryService.getBorrowedBooks(STUDENT_ID)).thenReturn(List.of(borrowedBook));

        String view = libraryController.showBorrowedBooks(STUDENT_ID, model);

        assertEquals("borrowed", view);
        verify(model).addAttribute("borrowedBooks", List.of(borrowedBook));
        verify(model).addAttribute("studentId", STUDENT_ID);
    }

    @Test
    void showBorrowedBooks_ReturnsBorrowedView_WithEmptyList() {
        when(libraryService.getBorrowedBooks(STUDENT_ID)).thenReturn(List.of());

        String view = libraryController.showBorrowedBooks(STUDENT_ID, model);

        assertEquals("borrowed", view);
        verify(model).addAttribute("borrowedBooks", List.of());
        verify(model).addAttribute("studentId", STUDENT_ID);
    }

    @Test
    void showBorrowedBooks_ReturnsLoginView_WhenAccountNotFound() {
        when(libraryService.getBorrowedBooks(STUDENT_ID))
                .thenThrow(new RuntimeException("Library account not found"));

        String view = libraryController.showBorrowedBooks(STUDENT_ID, model);

        assertEquals("login", view);
        verify(model).addAttribute("error", "Library account not found");
    }

    // -------------------------
    // POST /library/{studentId}/return/{bookId}
    // -------------------------

    @Test
    void returnBook_Success_RedirectsToBorrowed() {
        when(libraryService.returnBook(BOOK_ID)).thenReturn(borrowedBook);

        String view = libraryController.returnBook(STUDENT_ID, BOOK_ID, model);

        assertEquals("redirect:/library/" + STUDENT_ID + "/borrowed", view);
    }

    @Test
    void returnBook_ReturnsBorrowedView_WhenExceptionThrown() {
        when(libraryService.returnBook(BOOK_ID))
                .thenThrow(new RuntimeException("Borrowed book record not found"));

        String view = libraryController.returnBook(STUDENT_ID, BOOK_ID, model);

        assertEquals("borrowed", view);
        verify(model).addAttribute("error", "Borrowed book record not found");
    }
}