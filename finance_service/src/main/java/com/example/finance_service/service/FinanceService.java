package com.example.finance_service.service;

import com.example.finance_service.model.Invoice;
import com.example.finance_service.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for all finance related business logic.
 */
@Service
public class FinanceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    /**
     * Creates a new unpaid invoice and persists in the database.
     * @param invoiceNumber unique invoice reference generated.
     * @param studentId the human facing student ID.
     * @param amount the monetary amount to charge
     * @return the new invoice.
     */
    public Invoice createInvoice(String invoiceNumber, String studentId, Double amount) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setStudentId(studentId);
        invoice.setAmount(amount);
        invoice.setPaid(false);
        invoice.setCreatedAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    /**
     * Retrieves a single invoice by its invoice number.
     * @param invoiceNumber the unique invoice identifier.
     * @return
     */
    public Invoice getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    /**
     * Marks and invoice as paid and records the payment timestamp.
     * @param invoiceNumber
     * @return
     */
    public Invoice payInvoice(String invoiceNumber) {
        Invoice invoice = getInvoiceByNumber(invoiceNumber);
        if (invoice.isPaid()) {
            throw new RuntimeException("Invoice has already been paid");
        }
        invoice.setPaid(true);
        invoice.setPaidAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    /**
     * Checks if all invoices for a stundet have been paid.
     * @param studentId
     * @return
     */
    public boolean allInvoicePaid(String studentId) {
        return !invoiceRepository.existsByStudentIdAndPaidFalse(studentId);
    }

    /**
     * Retrieves all invoices raised against a specific student.
     * @param studentId
     * @return
     */
    public List<Invoice> getInvoiceForStudent(String studentId) {
        return invoiceRepository.findByStudentId(studentId);
    }
}
