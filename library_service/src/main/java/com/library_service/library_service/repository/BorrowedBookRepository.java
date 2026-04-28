package com.library_service.library_service.repository;

import com.library_service.library_service.model.BorrowedBook;
import com.library_service.library_service.model.LibraryAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for database operations on the BorrowedBook entity,
 */
@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long> {

    // Retrieves all borrow records associated with given library account.
    List<BorrowedBook> findByLibraryAccount(LibraryAccount account);

    // Counts the number of books currently borrowed by a student.
    int countByLibraryAccountAndReturnedAtIsNull(LibraryAccount account);

    // Checks whether a student currently has a specific book on loan.
    boolean existsByLibraryAccountAndIsbnAndReturnedAtIsNull(LibraryAccount account, String isbn);
}