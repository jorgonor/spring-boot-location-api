package com.jorgonor.locationapi;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
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

	@Value("${spring.threads.virtual.enabled}")
	boolean virtualThreadsEnabled;

	@PostConstruct
	public void onPostConstruct() {
		log.info("VIRTUAL THREADS ENABLED {}", virtualThreadsEnabled);
	}

}
