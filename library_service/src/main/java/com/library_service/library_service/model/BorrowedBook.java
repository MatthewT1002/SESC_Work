package com.library_service.library_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA Entity representing a single book borrowing transaction.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "borrowed_books")
public class BorrowedBook {

    // Auto incrementing internal primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The library account this borrow record belongs to.
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private LibraryAccount libraryAccount;

    // The ISBN (google code) of the borrowed book.
    @Column(nullable = false)
    private String isbn;

    // The title of the borrowed book.
    @Column(nullable = false)
    private String title;

    // The author of the borrowed book.
    @Column(nullable = false)
    private String author;

    // Timestamp when the book was borrowed.
    @Column(nullable = false)
    private LocalDateTime borrowedAt;

    // Timestamp the book is due back.
    @Column(nullable = false)
    private LocalDateTime dueAt;

    // Timestamp of returned book.
    private LocalDateTime returnedAt;

    // If the book is overdue or not.
    private boolean overdue;

    // The invoice number for the late fee.
    private String invoiceNumber;
}
