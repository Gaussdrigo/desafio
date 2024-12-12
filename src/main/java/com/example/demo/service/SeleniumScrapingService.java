package com.example.demo.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.example.demo.model.News;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeleniumScrapingService {

    public List<News> fetchNews(String query) {
        // Configurar ChromeDriver
        System.setProperty("webdriver.chrome.driver", "C:/tools/chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        List<News> newsList = new ArrayList<>();

        try {
            // Abrir la página de búsqueda
            String url = "https://www.abc.com.py/buscar/?q=" + query;
            System.out.println("Intentando abrir URL: " + url);
            driver.get(url);

            // Usar WebDriverWait para esperar que los elementos estén presentes
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".queryly_item_container")));
            System.out.println("Los elementos de los artículos se han cargado.");

            // Encontrar los artículos
            List<WebElement> articles = driver.findElements(By.cssSelector(".queryly_item_container"));
            System.out.println("Número de artículos encontrados: " + articles.size());

            // Procesar cada artículo
            for (WebElement article : articles) {
                System.out.println("Procesando un nuevo artículo...");
                try {
                    String titulo = article.findElement(By.cssSelector(".queryly_item_title a")).getText();
                    String resumen = article.findElement(By.cssSelector(".queryly_item_description")).getText();
                    String fecha = article.findElement(By.cssSelector(".queryly_item_pubdate")).getText();
                    String enlace = "https://www.abc.com.py"
                            + article.findElement(By.cssSelector(".queryly_item_title a")).getAttribute("href");

                    String imagen = "";
                    WebElement imageElement = article.findElement(By.cssSelector(".queryly_item_imagecontainer"));
                    if (imageElement != null) {
                        String style = imageElement.getAttribute("style");
                        if (style.contains("url(")) {
                            int startIndex = style.indexOf("url(") + 4;
                            int endIndex = style.indexOf(")", startIndex);
                            imagen = style.substring(startIndex, endIndex).replace("'", "");
                        }
                    }

                    System.out.println("Título: " + titulo);
                    System.out.println("Resumen: " + resumen);
                    System.out.println("Fecha: " + fecha);
                    System.out.println("Enlace: " + enlace);
                    System.out.println("Imagen: " + imagen);

                    newsList.add(new News(fecha, enlace, imagen, titulo, resumen));
                } catch (Exception e) {
                    System.err.println("Error al procesar un artículo: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error general durante el scraping: " + e.getMessage());
        } finally {
            System.out.println("Navegador permanecerá abierto para inspección.");
        }

        return newsList;
    }
}
