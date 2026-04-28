package com.example.student_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client responsible for all communication between Student Service and the Finance Service.
 */

@Component // Registers this class as a Spring bean so it can be injected.
public class FinanceClient {

    // Built-in HTTP client use to make REST calls to other services.
    private final RestTemplate restTemplate;

    // Base URL for the Finance Service.
    private final String financeServiceUrl = "http://finance-service:8081";

    // Manually instantiates RestTemplate.
    public FinanceClient() {
        restTemplate = new RestTemplate();
    }

    /**
     * Creates a new finance account for the given student in the Finance Service.
     * Called when a new student is registered in the Student Service.
     * @param studentId the unique identifier of the student.
     */
    public void createFianceAccount(String studentId) {
        try {
            // POST to /api/finance/account/{studentId} no request body or response body needed.
            restTemplate.postForObject(financeServiceUrl + "/api/finance/account/" + studentId,
                    null, // No request body.
                    Void.class // No Response needed.
            );
        }
        catch (Exception e) {
            // If the service is down skips creation to be retired later or done manually
            System.out.println("Finance Service unavailable - skipping account creation");
        }
    }

    /**
     *  Adds course fees to a students account.
     * @param studentId unique student identifier.
     * @param amount the fee amount to be charged.
     * @param invoiceNumber the unique invoice number.
     */
    public void addCourseFee(String studentId, Double amount, String invoiceNumber) {
        try {
            // POST to /api/finance/bill/{studentId} with amount and invoice number as query params.
            restTemplate.postForObject(
                    financeServiceUrl + "/api/finance/bill/" + studentId + "?amount=" + amount + "&invoiceNo=" + invoiceNumber,
                    null, // No request
                    Void.class // No response needed
            );
        } catch (Exception e) {
            // If the Finance Service is down skip over.
            System.out.println("Finance Service unavailable - skipping bill update");
        }
    }

    /**
     * Checks to see if the student has paid their bill.
     * @param studentId unique identifier.
     * @return True if bill is paid, False if not or service is down.
     */
    public boolean isBillPaid(String studentId) {
        try {
            // GET /api/finance/bill/{studentId}/paid — expects a Boolean response
            Boolean result = restTemplate.getForObject(financeServiceUrl + "/api/finance/bill/" + studentId + "/paid",
                    Boolean.class);
            // If the response is null treat as unpaid.
            return result != null && result;
        }
        catch (Exception e) {
            // If the Finance service is down default to unpaid.
            System.out.println("Finance Service unavailable - defaulting to unpaid");
            return false;
        }
    }

}
