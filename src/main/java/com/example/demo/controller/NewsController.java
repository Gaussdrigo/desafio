package com.example.demo.controller;

import com.example.demo.model.News;
import com.example.demo.service.ApiKeyService;
import com.example.demo.service.JsonFetcherService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/consulta")
public class NewsController {

    private final JsonFetcherService jsonFetcherService;
    private final ApiKeyService apiKeyService;

    public NewsController(JsonFetcherService jsonFetcherService, ApiKeyService apiKeyService) {
        this.jsonFetcherService = jsonFetcherService;
        this.apiKeyService = apiKeyService;
    }

    @GetMapping
    public ResponseEntity<?> getFormattedNews(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "false") boolean f,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "x-api-key", required = true) String apiKey,
            @RequestHeader(HttpHeaders.ACCEPT) String acceptHeader) {

        // Validar API Key
        if (apiKey == null || !apiKeyService.isValidApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("codigo", "g103", "error", "No autorizado"));
        }

        // Validar parámetro `q`
        if (q == null || q.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("codigo", "g268", "error", "Parámetros inválidos"));
        }

        // Obtener todas las noticias
        List<News> allResults = jsonFetcherService.fetchAndFormatJson(q);

        // Si no hay resultados
        if (allResults.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("codigo", "g267", "error", "No se encuentran noticias para el texto: " + q));
        }

        // Validar paginación
        if (page < 0 || size <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("codigo", "g268", "error",
                            "Parámetros de paginación inválidos. 'page' debe ser >= 0 y 'size' > 0."));
        }
        int start = page * size;
        if (start >= allResults.size()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("codigo", "g267", "error", "No hay más noticias disponibles para la paginación."));
        }
        int end = Math.min(start + size, allResults.size());
        List<News> paginatedResults = allResults.subList(start, end);

        // Procesar imágenes si `f=true`
        if (f) {
            paginatedResults.forEach(news -> {
                if (news.getEnlaceFoto() != null) {
                    String base64Content = jsonFetcherService.encodeImageToBase64(news.getEnlaceFoto());
                    if (base64Content != null) {
                        news.setContenidoFoto(base64Content);
                        news.setContentTypeFoto("image/jpeg");
                    }
                }
            });
        }

        // Generar respuesta en el formato solicitado
        switch (acceptHeader) {
            case MediaType.APPLICATION_JSON_VALUE:
                // Incluye metadatos de paginación
                Map<String, Object> response = new HashMap<>();
                response.put("currentPage", page);
                response.put("pageSize", size);
                response.put("totalResults", allResults.size());
                response.put("totalPages", (int) Math.ceil((double) allResults.size() / size));
                response.put("news", paginatedResults);
                return ResponseEntity.ok(response);

            case MediaType.APPLICATION_XML_VALUE:
                // Convertir a XML
                String xml = convertToXml(paginatedResults);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_XML)
                        .body(xml);

            case MediaType.TEXT_PLAIN_VALUE:
                // Texto plano
                String plainText = paginatedResults.stream()
                        .map(news -> news.getTitulo() + " - " + news.getFecha())
                        .collect(Collectors.joining("\n"));
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(plainText);

            case MediaType.TEXT_HTML_VALUE:
                // HTML simple
                String html = "<ul>" +
                        paginatedResults.stream()
                                .map(news -> "<li><b>" + news.getTitulo() + "</b> (" + news.getFecha() + ")</li>")
                                .collect(Collectors.joining())
                        +
                        "</ul>";
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(html);

            default:
                // Formato no soportado
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(Map.of("codigo", "g400", "error", "Formato no soportado: " + acceptHeader));
        }
    }

    // Método auxiliar para convertir a XML
    private String convertToXml(List<News> newsList) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<newsList>");
        for (News news : newsList) {
            xmlBuilder.append("<news>")
                    .append("<fecha>").append(news.getFecha()).append("</fecha>")
                    .append("<enlace>").append(news.getEnlace()).append("</enlace>")
                    .append("<enlaceFoto>").append(news.getEnlaceFoto()).append("</enlaceFoto>")
                    .append("<titulo>").append(news.getTitulo()).append("</titulo>")
                    .append("<resumen>").append(news.getResumen()).append("</resumen>")
                    .append("</news>");
        }
        xmlBuilder.append("</newsList>");
        return xmlBuilder.toString();
    }
}
