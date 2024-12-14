package com.example.demo;

import com.example.demo.service.HmacService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApificarApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApificarApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(HmacService hmacService) {
		return args -> {
			// Define una API Key para generar la firma
			String apiKey = "mi-api-key-valida";

			// Genera la firma usando el HmacService
			String signature = hmacService.signApiKey(apiKey);

			// Imprime la API Key y la firma en la consola
			System.out.println("=======================================");
			System.out.println("API Key generada: " + apiKey);
			System.out.println("Firma generada: " + signature);
			System.out.println("=======================================");
		};
	}
}
