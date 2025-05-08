package com.example.ioproject.controllers;


import com.example.ioproject.payload.request.CheckoutRequest;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

  @Value("${stripe.secret.key}")
  private String stripeSecretKey;

  @PostMapping("/create-checkout-session")
  public ResponseEntity<?> createCheckoutSession(@RequestBody CheckoutRequest request) {
    try {
      Stripe.apiKey = stripeSecretKey;

      SessionCreateParams params = SessionCreateParams.builder()
              .setMode(SessionCreateParams.Mode.PAYMENT)
              .setSuccessUrl("https://twojastrona.pl/success")
              .setCancelUrl("https://twojastrona.pl/cancel")
              .addLineItem(
                      SessionCreateParams.LineItem.builder()
                              .setQuantity(1L)
                              .setPriceData(
                                      SessionCreateParams.LineItem.PriceData.builder()
                                              .setCurrency("pln")
                                              .setUnitAmount(request.getAmount() * 100L) // amount in grosze
                                              .setProductData(
                                                      SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                              .setName("Wypożyczenie auta: " + request.getCarName())
                                                              .build()
                                              )
                                              .build()
                              )
                              .build()
              )
              .build();

      Session session = Session.create(params);

      Map<String, String> responseData = new HashMap<>();
      responseData.put("url", session.getUrl());

      return ResponseEntity.ok(responseData);

    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }
}
