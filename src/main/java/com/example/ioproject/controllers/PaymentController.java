package com.example.ioproject.controllers;


import com.example.ioproject.payload.request.CheckoutRequest;
import com.example.ioproject.security.services.ReservationService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import java.util.HashMap;
import java.util.Map;


/**
 * REST controller responsible for handling Stripe payment processing.
 * <p>
 * Provides endpoints for creating checkout sessions and processing Stripe webhook callbacks.
 * Integrates with {@link ReservationService} to manage reservation payment status.
 * </p>
 */
@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

  /**
   * Stripe secret key injected from application properties.
   */
  @Value("${stripe.secret.key}")
  private String stripeSecretKey;

  /**
   * Stripe webhook signing secret for verifying event authenticity.
   */
  @Value("${stripe.webhook.key}")
  private String stripeWebhookKey;

  @Autowired
  private ReservationService reservationService;

  /**
   * Creates a new Stripe Checkout session for processing a car rental payment.
   * <p>
   * The session includes payment details, pricing, product name and redirection URLs.
   * After creation, it returns the payment URL to the frontend.
   * </p>
   *
   * @param request the {@link CheckoutRequest} containing reservation and payment details
   * @return a response with the Stripe Checkout session URL or an error message
   */
  @PostMapping("/create-checkout-session")
  public ResponseEntity<?> createCheckoutSession(@RequestBody CheckoutRequest request) {
    try {
      Stripe.apiKey = stripeSecretKey;

      SessionCreateParams params = SessionCreateParams.builder()
              .setMode(SessionCreateParams.Mode.PAYMENT)
              .setSuccessUrl("http://localhost:5173/payment/success")
              .setCancelUrl("http://localhost:5173/payment/cancel")
              .setClientReferenceId(String.valueOf(request.getReservationId()))
              .addLineItem(
                      SessionCreateParams.LineItem.builder()
                              .setQuantity(1L)
                              .setPriceData(
                                      SessionCreateParams.LineItem.PriceData.builder()
                                              .setCurrency("pln")
                                              .setUnitAmount(request.getAmount() * 100L) // amount in grosze
                                              .setProductData(
                                                      SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                              .setName("Car rental: " + request.getCarName())
                                                              .build()
                                              )
                                              .build()
                              )
                              .build()
              )
              .build();

      Session session = Session.create(params);

      reservationService.setStripeSessionId(request.getReservationId(), session.getId());

      Map<String, String> responseData = new HashMap<>();
      responseData.put("url", session.getUrl());

      return ResponseEntity.ok(responseData);

    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  /**
   * Handles Stripe webhook events, primarily for confirming completed payments.
   * <p>
   * Verifies event signature, checks for {@code checkout.session.completed} events,
   * and marks the reservation as paid based on Stripe session or reservation ID.
   * </p>
   *
   * @param payload   the raw JSON event payload from Stripe
   * @param sigHeader the {@code Stripe-Signature} header used to verify the payload
   * @return an empty 200 OK response if handled successfully, or 400 Bad Request on error
   */
  @PostMapping("/webhook")
  public ResponseEntity<String> handleStripeWebhook(
          @RequestBody String payload,
          @RequestHeader("Stripe-Signature") String sigHeader) {
    String endpointSecret = stripeWebhookKey;

    try {
      Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

      if ("checkout.session.completed".equals(event.getType())) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();

        String reservationId = session.getClientReferenceId();
        String stripeSessionId = session.getId();

        if (reservationId != null) {
          reservationService.markAsPaid(Long.parseLong(reservationId));
          System.out.println("💰 Rezerwacja " + reservationId + " opłacona przez Stripe!");
        } else if (stripeSessionId != null) {
          reservationService.findByStripeSessionId(stripeSessionId)
                  .ifPresent(res -> reservationService.markAsPaid(res.getId()));
        }
      }

      return ResponseEntity.ok("");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body("");
    }
  }
}

