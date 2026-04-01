package com.example.finance_service.controller;

import com.example.finance_service.model.Invoice;
import com.example.finance_service.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance")
public class FinanceRestController {

    @Autowired
    private FinanceService financeService;

    @PostMapping("/bill/{studentId}")
    public ResponseEntity<Invoice> createInvoice(@PathVariable String studentId,
                                                 @RequestParam Double amount,
                                                 @RequestParam String invoiceNo) {
        try {
            Invoice invoice = financeService.createInvoice(invoiceNo, studentId, amount);
            return ResponseEntity.ok(invoice);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/bill/{studentId}/paid")
    public ResponseEntity<Boolean> allPaid(@PathVariable String studentId) {
        return ResponseEntity.ok(financeService.allInvoicePaid(studentId));
    }
}
