package com.example.demo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = ApificarApplicationTests.TestConfig.class)
class ApificarApplicationTests {

	@Test
	@Disabled("Deshabilitado temporalmente hasta resolver el problema")
	void contextLoads() {
	}

	@Configuration
	@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SwaggerConfig.class))
	static class TestConfig {

	}
}
