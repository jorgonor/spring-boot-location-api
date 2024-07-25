package com.jorgonor.locationapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class LocationApiTestContainersConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(LocationApiTestContainersConfiguration.class);

    @Profile("postgres")
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> pgvectorContainer() {
        LOG.info("Starting postgres test container.");
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withUrlParam("currentSchema", "location")
            .withUrlParam("binaryTransfer", "true")
            .withUrlParam("rewriteBatchedInserts", "true");
    }

    @Profile("mongo")
    @Bean
    @ServiceConnection
    MongoDBContainer mongoContainer() {
        LOG.info("Starting mongo test container.");
        return new MongoDBContainer("mongo:6.0.16");
    }

}