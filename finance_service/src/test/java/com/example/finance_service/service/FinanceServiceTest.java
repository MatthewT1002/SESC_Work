package com.example.finance_service.service;

import com.example.finance_service.model.Invoice;
import com.example.finance_service.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private FinanceService financeService;

    private Invoice invoice;

    private static final String STUDENT_ID = "c1234567";
    private static final String INVOICE_NUMBER = "INV-ABC123";
    private static final Double AMOUNT = 1200.00;

    @BeforeEach
    void setUp() {
        invoice = new Invoice();
        invoice.setInvoiceNumber(INVOICE_NUMBER);
        invoice.setStudentId(STUDENT_ID);
        invoice.setAmount(AMOUNT);
        invoice.setPaid(false);
        invoice.setCreatedAt(LocalDateTime.now());
    }

    // -------------------------
    // createInvoice
    // -------------------------

    @Test
    void createInvoice_Success() {
        when(invoiceRepository.save(org.mockito.ArgumentMatchers.any(Invoice.class))).thenReturn(invoice);

        Invoice result = financeService.createInvoice(INVOICE_NUMBER, STUDENT_ID, AMOUNT);

        assertNotNull(result);
        assertEquals(INVOICE_NUMBER, result.getInvoiceNumber());
        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals(AMOUNT, result.getAmount());
        assertFalse(result.isPaid());
        assertNotNull(result.getCreatedAt());
        verify(invoiceRepository).save(org.mockito.ArgumentMatchers.any(Invoice.class));
    }

    @Test
    void createInvoice_SavesInvoiceWithPaidFalse() {
        when(invoiceRepository.save(org.mockito.ArgumentMatchers.any(Invoice.class))).thenReturn(invoice);

        Invoice result = financeService.createInvoice(INVOICE_NUMBER, STUDENT_ID, AMOUNT);

        assertFalse(result.isPaid());
    }

    // -------------------------
    // getInvoiceByNumber
    // -------------------------

    @Test
    void getInvoiceByNumber_Success() {
        when(invoiceRepository.findByInvoiceNumber(INVOICE_NUMBER)).thenReturn(Optional.of(invoice));

        Invoice result = financeService.getInvoiceByNumber(INVOICE_NUMBER);

        assertNotNull(result);
        assertEquals(INVOICE_NUMBER, result.getInvoiceNumber());
        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals(AMOUNT, result.getAmount());
        verify(invoiceRepository).findByInvoiceNumber(INVOICE_NUMBER);
    }

    @Test
    void getInvoiceByNumber_ThrowsException_WhenInvoiceNotFound() {
        when(invoiceRepository.findByInvoiceNumber(INVOICE_NUMBER)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> financeService.getInvoiceByNumber(INVOICE_NUMBER));

        assertEquals("Invoice not found", exception.getMessage());
    }

    // -------------------------
    // payInvoice
    // -------------------------

    @Test
    void payInvoice_Success() {
        when(invoiceRepository.findByInvoiceNumber(INVOICE_NUMBER)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(org.mockito.ArgumentMatchers.any(Invoice.class))).thenReturn(invoice);

        Invoice result = financeService.payInvoice(INVOICE_NUMBER);

        assertNotNull(result);
        assertTrue(result.isPaid());
        assertNotNull(result.getPaidAt());
        verify(invoiceRepository).save(org.mockito.ArgumentMatchers.any(Invoice.class));
    }

    @Test
    void payInvoice_ThrowsException_WhenInvoiceAlreadyPaid() {
        invoice.setPaid(true);
        when(invoiceRepository.findByInvoiceNumber(INVOICE_NUMBER)).thenReturn(Optional.of(invoice));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> financeService.payInvoice(INVOICE_NUMBER));

        assertEquals("Invoice has already been paid", exception.getMessage());
        verify(invoiceRepository, never()).save(org.mockito.ArgumentMatchers.any(Invoice.class));
    }

    @Test
    void payInvoice_ThrowsException_WhenInvoiceNotFound() {
        when(invoiceRepository.findByInvoiceNumber(INVOICE_NUMBER)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> financeService.payInvoice(INVOICE_NUMBER));

        assertEquals("Invoice not found", exception.getMessage());
        verify(invoiceRepository, never()).save(org.mockito.ArgumentMatchers.any(Invoice.class));
    }

    // -------------------------
    // allInvoicePaid
    // -------------------------

    @Test
    void allInvoicePaid_ReturnsTrue_WhenAllInvoicesPaid() {
        when(invoiceRepository.existsByStudentIdAndPaidFalse(STUDENT_ID)).thenReturn(false);

        boolean result = financeService.allInvoicePaid(STUDENT_ID);

        assertTrue(result);
    }

    @Test
    void allInvoicePaid_ReturnsFalse_WhenUnpaidInvoicesExist() {
        when(invoiceRepository.existsByStudentIdAndPaidFalse(STUDENT_ID)).thenReturn(true);

        boolean result = financeService.allInvoicePaid(STUDENT_ID);

        assertFalse(result);
    }

    // -------------------------
    // getInvoiceForStudent
    // -------------------------

    @Test
    void getInvoiceForStudent_ReturnsListOfInvoices() {
        when(invoiceRepository.findByStudentId(STUDENT_ID)).thenReturn(List.of(invoice));

        List<Invoice> result = financeService.getInvoiceForStudent(STUDENT_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(invoice, result.get(0));
    }

    @Test
    void getInvoiceForStudent_ReturnsEmptyList_WhenNoInvoicesExist() {
        when(invoiceRepository.findByStudentId(STUDENT_ID)).thenReturn(List.of());

        List<Invoice> result = financeService.getInvoiceForStudent(STUDENT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}