package com.library_service.library_service.client;

import com.library_service.library_service.InvoiceGenerator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FinanceClient {

    private final RestTemplate restTemplate;
    private final String financeServiceUrl = "http://finance-service:8081";

    public FinanceClient() {
        this.restTemplate = new RestTemplate();
    }

    public void addOverdueFine(String studentId, Double amount, String invoiceNumber) {
        try {
            restTemplate.postForObject(
                    financeServiceUrl + "/api/finance/bill/" + studentId + "?amount=" + amount + "&invoiceNo=" + invoiceNumber,
                    null,
                    Void.class
            );
        } catch (Exception e) {
            System.out.println("Finance Service unavailable - skipping fine");
        }
    }
}
