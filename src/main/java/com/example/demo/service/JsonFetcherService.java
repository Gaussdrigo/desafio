package com.example.demo.service;

import com.example.demo.model.News;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Base64;

@Service
public class JsonFetcherService {

    public List<News> fetchAndFormatJson(String query) {
        String querylyKey = "33530b56c6aa4c20";
        System.out.println("Usando Queryly Key: " + querylyKey);

        String jsonUrl = "https://api.queryly.com/json.aspx?queryly_key=" + querylyKey +
                "&query=" + query +
                "&endindex=0&batchsize=20&callback=&showfaceted=true&extendeddatafields=creator,imageresizer,promo_image&timezoneoffset=240";
        System.out.println("Conectando a la URL del JSON: " + jsonUrl);

        List<News> formattedResults = new ArrayList<>();
        try {
            String jsonResponse = Jsoup.connect(jsonUrl)
                    .ignoreContentType(true)
                    .execute()
                    .body();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            JsonNode itemsNode = rootNode.path("items");
            if (itemsNode.isArray()) {
                for (JsonNode item : itemsNode) {
                    String fecha = item.path("pubdate").asText();
                    String enlace = "https://www.abc.com.py" + item.path("link").asText();
                    String enlaceFoto = item.path("image").asText();
                    String titulo = item.path("title").asText();
                    String resumen = item.path("description").asText();

                    News news = new News(fecha, enlace, enlaceFoto, titulo, resumen);
                    formattedResults.add(news);
                }
            }

        } catch (IOException e) {
            System.err.println("Error al conectar o procesar el JSON: " + e.getMessage());
        }

        return formattedResults;
    }

    /**
     * Convierte una imagen desde una URL en una cadena Base64.
     * 
     * @param imageUrl URL de la imagen a convertir.
     * @return Cadena Base64 de la imagen o null si ocurre un error.
     */
    public String encodeImageToBase64(String imageUrl) {
        try {
            BufferedImage image = ImageIO.read(new URL(imageUrl));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos); // Cambiar "png" al formato real de la imagen
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            System.err.println("Error al codificar la imagen: " + e.getMessage());
            return null;
        }
    }
}
