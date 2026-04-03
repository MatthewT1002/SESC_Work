package com.example.student_service;

import java.util.UUID;

public class InvoiceGenerator {
    public static String generate() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
