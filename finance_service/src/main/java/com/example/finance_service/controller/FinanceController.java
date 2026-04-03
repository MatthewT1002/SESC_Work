package com.example.finance_service.controller;

import com.example.finance_service.model.Invoice;
import com.example.finance_service.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @GetMapping("/")
    public String index() {
        return "search";
    }

    @GetMapping("/search")
    public String showSearchPage() {
        return "search";
    }

    @PostMapping("/search")
    public String searchInvoice(@RequestParam String invoiceNumber, Model model) {
        try {
            Invoice invoice = financeService.getInvoiceByNumber(invoiceNumber);
            model.addAttribute("invoice", invoice);
            return "invoice";
        }
        catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "search";
        }
    }

    @PostMapping("/pay/{invoiceNumber}")
    public String payInvoice(@PathVariable String invoiceNumber, Model model) {
        try {
            Invoice invoice = financeService.payInvoice(invoiceNumber);
            model.addAttribute("invoice", invoice);
            return "invoice";
        }
        catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "search";
        }
    }
}
