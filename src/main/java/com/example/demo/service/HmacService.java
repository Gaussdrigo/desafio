package com.example.demo.service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class HmacService {
    private static final String SECRET_KEY = "clave-secreta-segura";

    public String signApiKey(String apiKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] hmacBytes = mac.doFinal(apiKey.getBytes());
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al firmar el API Key: " + e.getMessage());
        }
    }

    public boolean verifySignature(String apiKey, String providedSignature) {
        String expectedSignature = signApiKey(apiKey);
        return expectedSignature.equals(providedSignature);
    }
}
