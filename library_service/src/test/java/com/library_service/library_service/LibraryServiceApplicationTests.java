package com.library_service.library_service.service;

import com.library_service.library_service.client.FinanceClient;
import com.library_service.library_service.client.GoogleBooksClient;
import com.library_service.library_service.model.BorrowedBook;
import com.library_service.library_service.model.LibraryAccount;
import com.library_service.library_service.repository.BorrowedBookRepository;
import com.library_service.library_service.repository.LibraryAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private LibraryAccountRepository libraryAccountRepository;

    @Mock
    private BorrowedBookRepository borrowedBookRepository;

    @Mock
    private GoogleBooksClient googleBooksClient;

    @Mock
    private FinanceClient financeClient;

    @InjectMocks
    private LibraryService libraryService;

    private LibraryAccount account;
    private BorrowedBook borrowedBook;

    private static final String STUDENT_ID = "c1234567";
    private static final String USERNAME = "johndoe";
    private static final String ISBN = "978-3-16-148410-0";
    private static final String TITLE = "Effective Java";
    private static final String AUTHOR = "Joshua Bloch";
    private static final Long BORROWED_BOOK_ID = 1L;

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
        borrowedBook.setBorrowedAt(LocalDateTime.now());
        borrowedBook.setDueAt(LocalDateTime.now().plusMinutes(5));
        borrowedBook.setOverdue(false);
    }

    // -------------------------
    // createAccount
    // -------------------------

    @Test
    void createAccount_Success() {
        when(libraryAccountRepository.existsByStudentId(STUDENT_ID)).thenReturn(false);
        when(libraryAccountRepository.save(org.mockito.ArgumentMatchers.any(LibraryAccount.class))).thenReturn(account);

        LibraryAccount result = libraryService.createAccount(STUDENT_ID, USERNAME);

        assertNotNull(result);
        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals(USERNAME, result.getUsername());
        verify(libraryAccountRepository).save(org.mockito.ArgumentMatchers.any(LibraryAccount.class));
    }

    @Test
    void createAccount_ThrowsException_WhenAccountAlreadyExists() {
        when(libraryAccountRepository.existsByStudentId(STUDENT_ID)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> libraryService.createAccount(STUDENT_ID, USERNAME));

        assertEquals("Account already exists for this student", exception.getMessage());
        verify(libraryAccountRepository, never()).save(org.mockito.ArgumentMatchers.any(LibraryAccount.class));
    }

    // -------------------------
    // getAccount
    // -------------------------

    @Test
    void getAccount_Success() {
        when(libraryAccountRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(account));

        LibraryAccount result = libraryService.getAccount(STUDENT_ID);

        assertNotNull(result);
        assertEquals(STUDENT_ID, result.getStudentId());
    }

    @Test
    void getAccount_ThrowsException_WhenAccountNotFound() {
        when(libraryAccountRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> libraryService.getAccount(STUDENT_ID));

        assertEquals("Library account not found", exception.getMessage());
    }

    // -------------------------
    // searchBooks
    // -------------------------

    @Test
    void searchBooks_ReturnsResults() {
        List<Map<String, String>> mockResults = List.of(
                Map.of("title", "Effective Java", "author", "Joshua Bloch")
        );
        when(googleBooksClient.searchBooks("Effective Java")).thenReturn(mockResults);

        List<Map<String, String>> result = libraryService.searchBooks("Effective Java");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Effective Java", result.get(0).get("title"));
    }

    @Test
    void searchBooks_ReturnsEmptyList_WhenNoBooksFound() {
        when(googleBooksClient.searchBooks("unknown book")).thenReturn(List.of());

        List<Map<String, String>> result = libraryService.searchBooks("unknown book");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // -------------------------
    // borrowBook
    // -------------------------

    @Test
    void borrowBook_Success() {
        when(libraryAccountRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(account));
        when(borrowedBookRepository.countByLibraryAccountAndReturnedAtIsNull(account)).thenReturn(Math.toIntExact((long) 2));
        when(borrowedBookRepository.existsByLibraryAccountAndIsbnAndReturnedAtIsNull(account, ISBN)).thenReturn(false);
        when(borrowedBookRepository.save(org.mockito.ArgumentMatchers.any(BorrowedBook.class))).thenReturn(borrowedBook);

        BorrowedBook result = libraryService.borrowBook(STUDENT_ID, ISBN, TITLE, AUTHOR);

        assertNotNull(result);
        assertEquals(ISBN, result.getIsbn());
        assertEquals(TITLE, result.getTitle());
        assertEquals(AUTHOR, result.getAuthor());
        assertFalse(result.isOverdue());
        verify(borrowedBookRepository).save(org.mockito.ArgumentMatchers.any(BorrowedBook.class));
    }

    @Test
    void borrowBook_ThrowsException_WhenAccountNotFound() {
        when(libraryAccountRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> libraryService.borrowBook(STUDENT_ID, ISBN, TITLE, AUTHOR));

        assertEquals("Library account not found", exception.getMessage());
        verify(borrowedBookRepository, never()).save(org.mockito.ArgumentMatchers.any(BorrowedBook.class));
    }

    @Test
    void borrowBook_ThrowsException_WhenMaxBorrowedBooksReached() {
        when(libraryAccountRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(account));
        when(borrowedBookRepository.countByLibraryAccountAndReturnedAtIsNull(account)).thenReturn(Math.toIntExact((long) 10));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> libraryService.borrowBook(STUDENT_ID, ISBN, TITLE, AUTHOR));

        assertEquals("You have reached the maximum of 10 borrowed books", exception.getMessage());
        verify(borrowedBookRepository, never()).save(org.mockito.ArgumentMatchers.any(BorrowedBook.class));
    }

    @Test
    void borrowBook_ThrowsException_WhenBookAlreadyBorrowed() {
        when(libraryAccountRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(account));
        when(borrowedBookRepository.countByLibraryAccountAndReturnedAtIsNull(account)).thenReturn(Math.toIntExact((long) 2));
        when(borrowedBookRepository.existsByLibraryAccountAndIsbnAndReturnedAtIsNull(account, ISBN)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> libraryService.borrowBook(STUDENT_ID, ISBN, TITLE, AUTHOR));

        assertEquals("You have already borrowed this book", exception.getMessage());
        verify(borrowedBookRepository, never()).save(org.mockito.ArgumentMatchers.any(BorrowedBook.class));
    }

    // -------------------------
    // returnBook
    // -------------------------

    @Test
    void returnBook_Success_WhenNotOverdue() {
        borrowedBook.setDueAt(LocalDateTime.now().plusHours(1));
        when(borrowedBookRepository.findById(BORROWED_BOOK_ID)).thenReturn(Optional.of(borrowedBook));
        when(borrowedBookRepository.save(org.mockito.ArgumentMatchers.any(BorrowedBook.class))).thenReturn(borrowedBook);

        BorrowedBook result = libraryService.returnBook(BORROWED_BOOK_ID);

        assertNotNull(result);
        assertNotNull(result.getReturnedAt());
        assertFalse(result.isOverdue());
        verify(financeClient, never()).addOverdueFine(anyString(), anyDouble(), anyString());
        verify(borrowedBookRepository).save(org.mockito.ArgumentMatchers.any(BorrowedBook.class));
    }

    @Test
    void returnBook_Success_WhenOverdue() {
        borrowedBook.setDueAt(LocalDateTime.now().minusHours(1));
        when(borrowedBookRepository.findById(BORROWED_BOOK_ID)).thenReturn(Optional.of(borrowedBook));
        when(borrowedBookRepository.save(org.mockito.ArgumentMatchers.any(BorrowedBook.class))).thenReturn(borrowedBook);

        BorrowedBook result = libraryService.returnBook(BORROWED_BOOK_ID);

        assertNotNull(result);
        assertTrue(result.isOverdue());
        verify(financeClient).addOverdueFine(eq(STUDENT_ID), eq(25.00), anyString());
        verify(borrowedBookRepository).save(org.mockito.ArgumentMatchers.any(BorrowedBook.class));
    }

    @Test
    void returnBook_ThrowsException_WhenBorrowedBookNotFound() {
        when(borrowedBookRepository.findById(BORROWED_BOOK_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> libraryService.returnBook(BORROWED_BOOK_ID));

        assertEquals("Borrowed book record not found", exception.getMessage());
        verify(borrowedBookRepository, never()).save(org.mockito.ArgumentMatchers.any(BorrowedBook.class));
    }

    // -------------------------
    // getBorrowedBooks
    // -------------------------

    @Test
    void getBorrowedBooks_Success() {
        when(libraryAccountRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(account));
        when(borrowedBookRepository.findByLibraryAccount(account)).thenReturn(List.of(borrowedBook));

        List<BorrowedBook> result = libraryService.getBorrowedBooks(STUDENT_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrowedBook, result.get(0));
    }

    @Test
    void getBorrowedBooks_ReturnsEmptyList_WhenNoBorrowedBooks() {
        when(libraryAccountRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(account));
        when(borrowedBookRepository.findByLibraryAccount(account)).thenReturn(List.of());

        List<BorrowedBook> result = libraryService.getBorrowedBooks(STUDENT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getBorrowedBooks_ThrowsException_WhenAccountNotFound() {
        when(libraryAccountRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> libraryService.getBorrowedBooks(STUDENT_ID));

        assertEquals("Library account not found", exception.getMessage());
    }
}
