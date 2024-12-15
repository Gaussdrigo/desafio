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
import java.util.List;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Autowired
    private final ApiKeyService apiKeyService;

    private final HmacService hmacService;

    // Rutas excluidas de la validación
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/", "/swagger-ui", "/v3/api-docs", "/swagger-ui/index.html");

    public ApiKeyFilter(ApiKeyService apiKeyService, HmacService hmacService) {
        this.apiKeyService = apiKeyService;
        this.hmacService = hmacService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Excluir rutas públicas
        if (isExcludedPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Validar encabezados
        String apiKey = request.getHeader("x-api-key");
        String signature = request.getHeader("x-api-signature");

        System.out.println("Encabezado x-api-key: " + apiKey);
        System.out.println("Encabezado x-api-signature: " + signature);

        if (apiKey == null || signature == null) {
            sendErrorResponse(response, "No autorizado. Faltan encabezados.");
            return;
        }

        if (!apiKeyService.isValidApiKey(apiKey) || !hmacService.verifySignature(apiKey, signature)) {
            sendErrorResponse(response, "No autorizado");
            return;
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    // Método para verificar rutas excluidas
    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    // Método para enviar respuestas de error
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"codigo\": \"g103\", \"error\": \"" + message + "\"}");
    }
}
