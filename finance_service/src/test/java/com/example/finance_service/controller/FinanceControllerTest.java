package com.example.finance_service.controller;

import com.example.finance_service.model.Invoice;
import com.example.finance_service.service.FinanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceControllerTest {

    @Mock
    private FinanceService financeService;

    @Mock
    private Model model;

    @InjectMocks
    private FinanceController financeController;

    private Invoice invoice;

    private static final String INVOICE_NUMBER = "INV-ABC123";
    private static final String STUDENT_ID = "c1234567";
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
    // GET /
    // -------------------------

    @Test
    void index_ReturnsSearchView() {
        String view = financeController.index();
        assertEquals("search", view);
    }

    // -------------------------
    // GET /search
    // -------------------------

    @Test
    void showSearchPage_ReturnsSearchView() {
        String view = financeController.showSearchPage();
        assertEquals("search", view);
    }

    // -------------------------
    // POST /search
    // -------------------------

    @Test
    void searchInvoice_ReturnsInvoiceView_WhenInvoiceFound() {
        when(financeService.getInvoiceByNumber(INVOICE_NUMBER)).thenReturn(invoice);

        String view = financeController.searchInvoice(INVOICE_NUMBER, model);

        assertEquals("invoice", view);
        verify(model).addAttribute("invoice", invoice);
    }

    @Test
    void searchInvoice_ReturnsSearchView_WhenInvoiceNotFound() {
        when(financeService.getInvoiceByNumber(INVOICE_NUMBER))
                .thenThrow(new RuntimeException("Invoice not found"));

        String view = financeController.searchInvoice(INVOICE_NUMBER, model);

        assertEquals("search", view);
        verify(model).addAttribute("error", "Invoice not found");
    }

    // -------------------------
    // POST /pay/{invoiceNumber}
    // -------------------------

    @Test
    void payInvoice_ReturnsInvoiceView_WhenPaymentSuccessful() {
        invoice.setPaid(true);
        invoice.setPaidAt(LocalDateTime.now());
        when(financeService.payInvoice(INVOICE_NUMBER)).thenReturn(invoice);

        String view = financeController.payInvoice(INVOICE_NUMBER, model);

        assertEquals("invoice", view);
        verify(model).addAttribute("invoice", invoice);
        assertTrue(invoice.isPaid());
        assertNotNull(invoice.getPaidAt());
    }

    @Test
    void payInvoice_ReturnsSearchView_WhenInvoiceAlreadyPaid() {
        when(financeService.payInvoice(INVOICE_NUMBER))
                .thenThrow(new RuntimeException("Invoice has already been paid"));

        String view = financeController.payInvoice(INVOICE_NUMBER, model);

        assertEquals("search", view);
        verify(model).addAttribute("error", "Invoice has already been paid");
    }

    @Test
    void payInvoice_ReturnsSearchView_WhenInvoiceNotFound() {
        when(financeService.payInvoice(INVOICE_NUMBER))
                .thenThrow(new RuntimeException("Invoice not found"));

        String view = financeController.payInvoice(INVOICE_NUMBER, model);

        assertEquals("search", view);
        verify(model).addAttribute("error", "Invoice not found");
    }
}