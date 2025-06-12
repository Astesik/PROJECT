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


@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

  @Value("${stripe.secret.key}")
  private String stripeSecretKey;

  @Value("${stripe.webhook.key}")
  private String stripeWebhookKey;

  @Autowired
  private ReservationService reservationService;

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

  @PostMapping("/webhook")
  public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
    String endpointSecret = stripeWebhookKey;

    try {
      Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

      // Obsługa eventu checkout.session.completed
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
