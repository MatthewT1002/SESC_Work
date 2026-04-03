package com.example.student_service.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FinanceClient financeClient;

    private static final String BASE_URL = "http://finance-service:8081";
    private static final String STUDENT_ID = "c1234567";
    private static final String INVOICE_NUMBER = "INV-ABC123";
    private static final Double AMOUNT = 1200.00;

    // -------------------------
    // createFinanceAccount
    // -------------------------

    @Test
    void createFinanceAccount_Success() {
        when(restTemplate.postForObject(
                BASE_URL + "/api/finance/account/" + STUDENT_ID,
                null,
                Void.class
        )).thenReturn(null);

        assertDoesNotThrow(() -> financeClient.createFianceAccount(STUDENT_ID));

        verify(restTemplate).postForObject(
                BASE_URL + "/api/finance/account/" + STUDENT_ID,
                null,
                Void.class
        );
    }

    @Test
    void createFinanceAccount_DoesNotThrow_WhenServiceUnavailable() {
        when(restTemplate.postForObject(
                BASE_URL + "/api/finance/account/" + STUDENT_ID,
                null,
                Void.class
        )).thenThrow(new RestClientException("Service unavailable"));

        assertDoesNotThrow(() -> financeClient.createFianceAccount(STUDENT_ID));
    }

    // -------------------------
    // addCourseFee
    // -------------------------

    @Test
    void addCourseFee_Success() {
        String expectedUrl = BASE_URL + "/api/finance/bill/" + STUDENT_ID
                + "?amount=" + AMOUNT + "&invoiceNo=" + INVOICE_NUMBER;

        when(restTemplate.postForObject(expectedUrl, null, Void.class)).thenReturn(null);

        assertDoesNotThrow(() -> financeClient.addCourseFee(STUDENT_ID, AMOUNT, INVOICE_NUMBER));

        verify(restTemplate).postForObject(expectedUrl, null, Void.class);
    }

    @Test
    void addCourseFee_DoesNotThrow_WhenServiceUnavailable() {
        String expectedUrl = BASE_URL + "/api/finance/bill/" + STUDENT_ID
                + "?amount=" + AMOUNT + "&invoiceNo=" + INVOICE_NUMBER;

        when(restTemplate.postForObject(expectedUrl, null, Void.class))
                .thenThrow(new RestClientException("Service unavailable"));

        assertDoesNotThrow(() -> financeClient.addCourseFee(STUDENT_ID, AMOUNT, INVOICE_NUMBER));
    }

    // -------------------------
    // isBillPaid
    // -------------------------

    @Test
    void isBillPaid_ReturnsTrue_WhenBillIsPaid() {
        String expectedUrl = BASE_URL + "/api/finance/bill/" + STUDENT_ID + "/paid";
        when(restTemplate.getForObject(expectedUrl, Boolean.class)).thenReturn(true);

        boolean result = financeClient.isBillPaid(STUDENT_ID);

        assertTrue(result);
        verify(restTemplate).getForObject(expectedUrl, Boolean.class);
    }

    @Test
    void isBillPaid_ReturnsFalse_WhenBillIsNotPaid() {
        String expectedUrl = BASE_URL + "/api/finance/bill/" + STUDENT_ID + "/paid";
        when(restTemplate.getForObject(expectedUrl, Boolean.class)).thenReturn(false);

        boolean result = financeClient.isBillPaid(STUDENT_ID);

        assertFalse(result);
    }

    @Test
    void isBillPaid_ReturnsFalse_WhenResponseIsNull() {
        String expectedUrl = BASE_URL + "/api/finance/bill/" + STUDENT_ID + "/paid";
        when(restTemplate.getForObject(expectedUrl, Boolean.class)).thenReturn(null);

        boolean result = financeClient.isBillPaid(STUDENT_ID);

        assertFalse(result);
    }

    @Test
    void isBillPaid_ReturnsFalse_WhenServiceUnavailable() {
        String expectedUrl = BASE_URL + "/api/finance/bill/" + STUDENT_ID + "/paid";
        when(restTemplate.getForObject(expectedUrl, Boolean.class))
                .thenThrow(new RestClientException("Service unavailable"));

        boolean result = financeClient.isBillPaid(STUDENT_ID);

        assertFalse(result);
    }
}