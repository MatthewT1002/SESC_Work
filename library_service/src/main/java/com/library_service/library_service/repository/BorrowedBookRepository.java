package com.library_service.library_service.repository;

import com.library_service.library_service.model.BorrowedBook;
import com.library_service.library_service.model.LibraryAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long> {
    List<BorrowedBook> findByLibraryAccount(LibraryAccount account);
    int countByLibraryAccountAndReturnedAtIsNull(LibraryAccount account);
    boolean existsByLibraryAccountAndIsbnAndReturnedAtIsNull(LibraryAccount account, String isbn);
}