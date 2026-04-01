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

@Service
public class LibraryService {

    @Autowired
    private LibraryAccountRepository libraryAccountRepository;

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    @Autowired
    private GoogleBooksClient googleBooksClient;

    @Autowired
    private FinanceClient financeClient;

    public LibraryAccount createAccount(String studentId, String username) {
        if (libraryAccountRepository.existsByStudentId(studentId)) {
            throw new RuntimeException("Account already exists for this student");
        }
        LibraryAccount account = new LibraryAccount();
        account.setStudentId(studentId);
        account.setUsername(username);
        return libraryAccountRepository.save(account);
    }

    public LibraryAccount getAccount(String studentId) {
        return libraryAccountRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Library account not found"));
    }

    public List<Map<String, String>> searchBooks(String query) {
        return googleBooksClient.searchBooks(query);
    }

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

    public List<BorrowedBook> getBorrowedBooks(String studentId) {
        LibraryAccount account = getAccount(studentId);
        return borrowedBookRepository.findByLibraryAccount(account);
    }
}
