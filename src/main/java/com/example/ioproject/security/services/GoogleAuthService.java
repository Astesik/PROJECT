package com.example.ioproject.security.services;

import com.example.ioproject.payload.request.GoogleRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class GoogleAuthService {

    private final GoogleIdTokenVerifier verifier;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${GOOGLE_AUTH_SECRET_KEY}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    public GoogleAuthService(@Value("${google.client.id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))



                .build();
    }

    public String retriveIdToken(GoogleRequest request) {
        String code = request.getCode();
        String scope = request.getScope();
        String grantType = "authorization_code";

        try {
            RestTemplate restTemplate = new RestTemplate();

            // headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded");

            // parameters
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("scope", scope);
            params.add("grant_type", grantType);

            // create and send post request
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_TOKEN_URL, requestEntity, String.class);

            // process response from google API
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode responseJson = new ObjectMapper().readTree(response.getBody());
                String accessToken = responseJson.get("access_token").asText();
                String idToken = responseJson.get("id_token").asText();

//                System.out.println("Access Token: " + accessToken);
//                System.out.println("ID Token: " + idToken);
                return idToken;
            } else {
                System.err.println("Error exchanging code: " + response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            System.err.println("Exception during token exchange: " + e.getMessage());
            return null;
        }
    }

    public IdToken.Payload verify(String frontendTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(frontendTokenString);
//            System.out.println("Verifying token: " + frontendTokenString);
            if (idToken != null) {
//                System.out.println("Token payload: " + idToken.getPayload());
                return idToken.getPayload();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
