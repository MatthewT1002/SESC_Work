package com.library_service.library_service.repository;

import com.library_service.library_service.model.LibraryAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for database operations on the LibraryAccount entity.
 */
@Repository
public interface LibraryAccountRepository extends JpaRepository<LibraryAccount, Long> {

    // Find a library account by the students unique identifier.
    Optional<LibraryAccount> findByStudentId(String studentId);

    // Checks if account already exists.
    boolean existsByStudentId(String studentId);
}
