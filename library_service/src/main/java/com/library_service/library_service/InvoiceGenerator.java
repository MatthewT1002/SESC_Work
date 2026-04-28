package com.library_service.library_service;

import java.util.UUID;

// Invoice generator to generate invoice with specific constraints.
public class InvoiceGenerator {
    public static String generate() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
