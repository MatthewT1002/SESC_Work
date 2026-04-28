package com.library_service.library_service.service;

import com.library_service.library_service.InvoiceGenerator;
import com.library_service.library_service.client.FinanceClient;
import com.library_service.library_service.client.GoogleBooksClient;
import com.library_service.library_service.model.BorrowedBook;
import com.library_service.library_service.model.LibraryAccount;
import com.library_service.library_service.repository.BorrowedBookRepository;
import com.library_service.library_service.repository.LibraryAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service layer for all library related business logic.
 */
@Service
public class LibraryService {

    @Autowired
    private LibraryAccountRepository libraryAccountRepository;

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    // Client for the Google book public api.
    @Autowired
    private GoogleBooksClient googleBooksClient;

    // HTTP client for the Finance Service.
    @Autowired
    private FinanceClient financeClient;

    // Creates a new library account for a student.
    public LibraryAccount createAccount(String studentId, String username) {
        if (libraryAccountRepository.existsByStudentId(studentId)) {
            throw new RuntimeException("Account already exists for this student");
        }
        LibraryAccount account = new LibraryAccount();
        account.setStudentId(studentId);
        account.setUsername(username);
        return libraryAccountRepository.save(account);
    }

    // Retrieves a library account by student id.
    public LibraryAccount getAccount(String studentId) {
        return libraryAccountRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Library account not found"));
    }

    // Searches for books via the Google Books API.
    public List<Map<String, String>> searchBooks(String query) {
        return googleBooksClient.searchBooks(query);
    }

    // Records a new book borrowing transaction for a student. Won't allow more than 10 books and 2 of the same book.
    public BorrowedBook borrowBook(String studentId, String isbn, String title, String author) {
        LibraryAccount account = getAccount(studentId);

        if (borrowedBookRepository.countByLibraryAccountAndReturnedAtIsNull(account) >= 10) {
            throw new RuntimeException("You have reached the maximum of 10 borrowed books");
        }

        if (borrowedBookRepository.existsByLibraryAccountAndIsbnAndReturnedAtIsNull(account, isbn)) {
            throw new RuntimeException("You have already borrowed this book");
        }

        BorrowedBook book = new BorrowedBook();
        book.setLibraryAccount(account);
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setBorrowedAt(LocalDateTime.now());
        book.setDueAt(LocalDateTime.now().plusMinutes(5));
        book.setOverdue(false);

        return borrowedBookRepository.save(book);
    }

    // Records the return of a borrowed book and handles overdue fines.
    public BorrowedBook returnBook(Long borrowedBookId) {
        BorrowedBook book = borrowedBookRepository.findById(borrowedBookId)
                .orElseThrow(() -> new RuntimeException("Borrowed book record not found"));

        LocalDateTime now = LocalDateTime.now();
        book.setReturnedAt(now);

        if (now.isAfter(book.getDueAt())) {
            book.setOverdue(true);
            String invoiceNumber = InvoiceGenerator.generate();
            book.setInvoiceNumber(invoiceNumber);
            financeClient.addOverdueFine(book.getLibraryAccount().getStudentId(), 25.00, invoiceNumber);
        }

        return borrowedBookRepository.save(book);
    }

    // Retrieves all borrow records (active and returned) for a given student.
    public List<BorrowedBook> getBorrowedBooks(String studentId) {
        LibraryAccount account = getAccount(studentId);
        return borrowedBookRepository.findByLibraryAccount(account);
    }
}
