package com.example.finance_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * JPA Entity representing an invoice (charge) raised against a student.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoice {

    /**
     * Auto incremented internal primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique invoice reference.
     */
    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    /**
     * The human facing student identifier.
     */
    @Column(nullable = false)
    private String studentId;

    /**
     * The monetary amount.
     */
    @Column(nullable = false)
    private Double amount;

    /**
     *  Whether the invoice has been
     */
    @Column(nullable = false)
    private boolean paid;

    /**
     * Timestamp of when the invoice was created
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of when the invoice was paid.
     */
    private LocalDateTime paidAt;
}
