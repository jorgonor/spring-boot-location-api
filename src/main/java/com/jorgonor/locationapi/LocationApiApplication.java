package com.jorgonor.locationapi;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LocationApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocationApiApplication.class, args);
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer customizeObjectMapper() {
		return builder -> {
			builder.modulesToInstall(new JavaTimeModule());
			builder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			builder.timeZone("UTC");
		};
	}
}
