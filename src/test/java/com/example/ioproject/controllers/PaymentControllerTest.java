package com.example.ioproject.controllers;

import com.example.ioproject.payload.request.CheckoutRequest;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session.CustomerDetails;
import com.stripe.net.Webhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false) // Wyłączenie Security do testu webhooka
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHandleStripeWebhook_withValidPayload() throws Exception {
        // Przygotuj sample JSON webhook payload
        String payload = """
                {
                  "id": "evt_test_webhook",
                  "object": "event",
                  "api_version": "2020-08-27",
                  "created": 1234567890,
                  "data": {
                    "object": {
                      "id": "cs_test_session_123",
                      "object": "checkout.session",
                      "amount_total": 10000,
                      "customer_details": {
                        "email": "test@example.com"
                      }
                    }
                  },
                  "livemode": false,
                  "pending_webhooks": 1,
                  "request": {
                    "id": "req_test",
                    "idempotency_key": null
                  },
                  "type": "checkout.session.completed"
                }
                """;

        // Mock Webhook.constructEvent żeby nie wywoływał realnego sprawdzania signature (bo to by się wywaliło)
        Event event = Mockito.mock(Event.class);
        when(event.getType()).thenReturn("checkout.session.completed");

        Session session = mock(Session.class);
        when(session.getId()).thenReturn("cs_test_session_123");
        when(session.getAmountTotal()).thenReturn(10000L);

        CustomerDetails customerDetails = mock(CustomerDetails.class);
        when(customerDetails.getEmail()).thenReturn("test@example.com");
        when(session.getCustomerDetails()).thenReturn(customerDetails);

        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        when(deserializer.getObject()).thenReturn(Optional.of(session));

        when(event.getDataObjectDeserializer()).thenReturn(deserializer);

        try (MockedStatic<Webhook> mockedWebhook = Mockito.mockStatic(Webhook.class)) {
            mockedWebhook.when(() -> Webhook.constructEvent(any(), any(), any()))
                    .thenReturn(event);

            mockMvc.perform(post("/api/payment/webhook")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload)
                            .header("Stripe-Signature", "test_signature"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }
    }
}

