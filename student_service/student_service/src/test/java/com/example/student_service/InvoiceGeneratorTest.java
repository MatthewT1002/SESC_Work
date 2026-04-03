package com.example.student_service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceGeneratorTest {

    @Test
    void generate_ReturnsInvoiceWithCorrectPrefix() {
        String invoice = InvoiceGenerator.generate();
        assertTrue(invoice.startsWith("INV-"));
    }

    @Test
    void generate_ReturnsInvoiceWithCorrectLength() {
        String invoice = InvoiceGenerator.generate();
        // "INV-" (4 chars) + 8 character UUID substring = 12 chars total
        assertEquals(12, invoice.length());
    }

    @Test
    void generate_ReturnsInvoiceInUpperCase() {
        String invoice = InvoiceGenerator.generate();
        String suffix = invoice.substring(4);
        assertEquals(suffix.toUpperCase(), suffix);
    }

    @Test
    void generate_ReturnsUniqueInvoiceNumbers() {
        String invoice1 = InvoiceGenerator.generate();
        String invoice2 = InvoiceGenerator.generate();
        assertNotEquals(invoice1, invoice2);
    }

    @Test
    void generate_ReturnsInvoiceWithOnlyAlphanumericCharsAfterPrefix() {
        String invoice = InvoiceGenerator.generate();
        String suffix = invoice.substring(4);
        assertTrue(suffix.matches("[A-Z0-9]+"));
    }
}