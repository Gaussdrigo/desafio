package com.example.demo.controller;

import com.example.demo.model.News;
import com.example.demo.service.ApiKeyService;
import com.example.demo.service.JsonFetcherService;
import io.swagger.v3.oas.annotations.Parameter;
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

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
            MediaType.TEXT_PLAIN_VALUE, MediaType.TEXT_HTML_VALUE })
    public ResponseEntity<?> getFormattedNews(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "false") boolean f,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "API Key requerida", required = true) @RequestHeader(value = "x-api-key") String apiKey,
            @Parameter(description = "Tipo de formato: application/json, application/xml, text/plain, text/html", required = false) @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = MediaType.APPLICATION_JSON_VALUE) String acceptHeader) {

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
        if (allResults.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("codigo", "g267", "error", "No se encuentran noticias para el texto: " + q));
        }

        // Paginación
        if (page < 0 || size <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("codigo", "g268", "error", "Parámetros de paginación inválidos."));
        }
        int start = page * size;
        int end = Math.min(start + size, allResults.size());
        if (start >= allResults.size()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("codigo", "g267", "error", "No hay más noticias disponibles."));
        }
        List<News> paginatedResults = allResults.subList(start, end);

        // Procesar imágenes si `f=true`
        if (f) {
            paginatedResults.forEach(news -> {
                if (news.getEnlaceFoto() != null) {
                    String base64Content = jsonFetcherService.encodeImageToBase64(news.getEnlaceFoto());
                    news.setContenidoFoto(base64Content);
                    news.setContentTypeFoto("image/jpeg");
                }
            });
        }

        // Respuesta basada en el encabezado "Accept"
        return switch (acceptHeader) {
            case MediaType.APPLICATION_JSON_VALUE -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(buildJsonResponse(page, size, allResults.size(), paginatedResults));

            case MediaType.APPLICATION_XML_VALUE -> ResponseEntity.ok().contentType(MediaType.APPLICATION_XML)
                    .body(convertToXml(paginatedResults));

            case MediaType.TEXT_PLAIN_VALUE -> ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
                    .body(paginatedResults.stream()
                            .map(news -> news.getTitulo() + " - " + news.getFecha())
                            .collect(Collectors.joining("\n")));

            case MediaType.TEXT_HTML_VALUE -> ResponseEntity.ok().contentType(MediaType.TEXT_HTML)
                    .body(buildHtmlResponse(paginatedResults));

            default -> ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(Map.of("codigo", "g400", "error", "Formato no soportado: " + acceptHeader));
        };
    }

    private Map<String, Object> buildJsonResponse(int page, int size, int totalResults, List<News> results) {
        Map<String, Object> response = new HashMap<>();
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("totalResults", totalResults);
        response.put("totalPages", (int) Math.ceil((double) totalResults / size));
        response.put("news", results);
        return response;
    }

    private String convertToXml(List<News> newsList) {
        StringBuilder xmlBuilder = new StringBuilder("<newsList>");
        for (News news : newsList) {
            xmlBuilder.append("<news>")
                    .append("<fecha>").append(news.getFecha()).append("</fecha>")
                    .append("<titulo>").append(news.getTitulo()).append("</titulo>")
                    .append("</news>");
        }
        xmlBuilder.append("</newsList>");
        return xmlBuilder.toString();
    }

    private String buildHtmlResponse(List<News> results) {
        return "<ul>" + results.stream()
                .map(news -> "<li><b>" + news.getTitulo() + "</b> (" + news.getFecha() + ")</li>")
                .collect(Collectors.joining()) + "</ul>";
    }
}
