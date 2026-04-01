package com.example.student_service.client;

import com.example.student_service.InvoiceGenerator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FinanceClient {

    private final RestTemplate restTemplate;
    private final String financeServiceUrl = "http://finance-service:8081";

    public FinanceClient() {
        restTemplate = new RestTemplate();
    }

    public void createFianceAccount(String studentId) {
        try {
            restTemplate.postForObject(financeServiceUrl + "/api/finance/account/" + studentId,
                    null,
                    Void.class
            );
        }
        catch (Exception e) {
            System.out.println("Finance Service unavailable - skipping account creation");
        }
    }

    public void addCourseFee(String studentId, Double amount, String invoiceNumber) {
        try {
            restTemplate.postForObject(
                    financeServiceUrl + "/api/finance/bill/" + studentId + "?amount=" + amount + "&invoiceNo=" + invoiceNumber,
                    null,
                    Void.class
            );
        } catch (Exception e) {
            System.out.println("Finance Service unavailable - skipping bill update");
        }
    }

    public boolean isBillPaid(String studentId) {
        try {
            Boolean result = restTemplate.getForObject(financeServiceUrl + "/api/finance/bill/" + studentId + "/paid",
                    Boolean.class);
            return result != null && result;
        }
        catch (Exception e) {
            System.out.println("Finance Service unavailable - defaulting to unpaid");
            return false;
        }
    }

}
