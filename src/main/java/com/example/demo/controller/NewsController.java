package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.News;
import com.example.demo.service.SeleniumScrapingService;

import java.util.List;

@RestController
@RequestMapping("/consulta")
public class NewsController {

    private final SeleniumScrapingService scrapingService;

    public NewsController(SeleniumScrapingService scrapingService) {
        this.scrapingService = scrapingService;
    }

    @GetMapping
    public ResponseEntity<?> getNews(@RequestParam(required = false) String q) {
        try {
            // Validar si el parámetro 'q' está presente y no está vacío
            if (q == null || q.isBlank()) {
                return ResponseEntity.badRequest().body("El parámetro 'q' es obligatorio y no puede estar vacío.");
            }

            // Realizar el scraping y obtener los resultados
            List<News> newsList = scrapingService.fetchNews(q);

            // Validar si se encontraron noticias
            if (newsList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontraron noticias para el texto: " + q);
            }

            // Retornar la lista de noticias si todo está correcto
            return ResponseEntity.ok(newsList);

        } catch (Exception e) {
            // Capturar cualquier excepción y retornar un error interno
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al procesar la solicitud. Intente nuevamente más tarde.");
        }
    }
}
