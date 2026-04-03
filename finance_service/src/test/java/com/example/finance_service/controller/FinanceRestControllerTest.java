package com.example.finance_service.controller;

import com.example.finance_service.model.Invoice;
import com.example.finance_service.service.FinanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceRestControllerTest {

    @Mock
    private FinanceService financeService;

    @InjectMocks
    private FinanceRestController financeRestController;

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
    // POST /api/finance/bill/{studentId}
    // -------------------------

    @Test
    void createInvoice_Returns200_WhenInvoiceCreatedSuccessfully() {
        when(financeService.createInvoice(INVOICE_NUMBER, STUDENT_ID, AMOUNT)).thenReturn(invoice);

        ResponseEntity<Invoice> response = financeRestController.createInvoice(STUDENT_ID, AMOUNT, INVOICE_NUMBER);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(INVOICE_NUMBER, response.getBody().getInvoiceNumber());
        assertEquals(STUDENT_ID, response.getBody().getStudentId());
        assertEquals(AMOUNT, response.getBody().getAmount());
        assertFalse(response.getBody().isPaid());
        verify(financeService).createInvoice(INVOICE_NUMBER, STUDENT_ID, AMOUNT);
    }

    @Test
    void createInvoice_Returns400_WhenExceptionThrown() {
        when(financeService.createInvoice(INVOICE_NUMBER, STUDENT_ID, AMOUNT))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<Invoice> response = financeRestController.createInvoice(STUDENT_ID, AMOUNT, INVOICE_NUMBER);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // -------------------------
    // GET /api/finance/bill/{studentId}/paid
    // -------------------------

    @Test
    void allPaid_Returns200WithTrue_WhenAllInvoicesPaid() {
        when(financeService.allInvoicePaid(STUDENT_ID)).thenReturn(true);

        ResponseEntity<Boolean> response = financeRestController.allPaid(STUDENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody());
        verify(financeService).allInvoicePaid(STUDENT_ID);
    }

    @Test
    void allPaid_Returns200WithFalse_WhenUnpaidInvoicesExist() {
        when(financeService.allInvoicePaid(STUDENT_ID)).thenReturn(false);

        ResponseEntity<Boolean> response = financeRestController.allPaid(STUDENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody());
        verify(financeService).allInvoicePaid(STUDENT_ID);
    }
}