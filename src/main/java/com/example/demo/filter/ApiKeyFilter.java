package com.example.demo.filter;

import com.example.demo.service.ApiKeyService;
import com.example.demo.service.HmacService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Autowired
    private final ApiKeyService apiKeyService;

    private final HmacService hmacService;

    public ApiKeyFilter(ApiKeyService apiKeyService, HmacService hmacService) {
        this.apiKeyService = apiKeyService;
        this.hmacService = hmacService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Depuración: Mostrar los encabezados recibidos
        System.out.println("Encabezado x-api-key: " + request.getHeader("x-api-key"));
        System.out.println("Encabezado x-api-signature: " + request.getHeader("x-api-signature"));

        String apiKey = request.getHeader("x-api-key");
        String signature = request.getHeader("x-api-signature");

        if (apiKey == null || signature == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"codigo\": \"g103\", \"error\": \"No autorizado. Faltan encabezados.\"}");
            return;
        }
        System.out.println("paso aqui: ");
        if (!apiKeyService.isValidApiKey(apiKey) || !hmacService.verifySignature(apiKey, signature)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"codigo\": \"g103\", \"error\": \"No autorizado\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

}
