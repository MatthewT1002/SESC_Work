package com.library_service.library_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA Entity representing a students library account.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "library_accounts")
public class LibraryAccount {

    // Auto incremented private key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The human facing student identifier.
    @Column(unique = true, nullable = false)
    private String studentId;

    // The students username, carried over from the Student Service.
    @Column(unique = true, nullable = false)
    private String username;
}
