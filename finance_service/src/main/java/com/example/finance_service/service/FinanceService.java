package com.example.finance_service.service;

import com.example.finance_service.model.Invoice;
import com.example.finance_service.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinanceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public Invoice createInvoice(String invoiceNumber, String studentId, Double amount) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setStudentId(studentId);
        invoice.setAmount(amount);
        invoice.setPaid(false);
        invoice.setCreatedAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    public Invoice getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    public Invoice payInvoice(String invoiceNumber) {
        Invoice invoice = getInvoiceByNumber(invoiceNumber);
        if (invoice.isPaid()) {
            throw new RuntimeException("Invoice has already been paid");
        }
        invoice.setPaid(true);
        invoice.setPaidAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    public boolean allInvoicePaid(String studentId) {
        return !invoiceRepository.existsByStudentIdAndPaidFalse(studentId);
    }

    public List<Invoice> getInvoiceForStudent(String studentId) {
        return invoiceRepository.findByStudentId(studentId);
    }
}
