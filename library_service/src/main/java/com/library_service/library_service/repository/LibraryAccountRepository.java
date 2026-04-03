package com.library_service.library_service.repository;

import com.library_service.library_service.model.LibraryAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LibraryAccountRepository extends JpaRepository<LibraryAccount, Long> {
    Optional<LibraryAccount> findByStudentId(String studentId);
    boolean existsByStudentId(String studentId);
}
