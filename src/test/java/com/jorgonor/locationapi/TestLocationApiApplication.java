package com.jorgonor.locationapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestLocationApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(LocationApiApplication::main).with(LocationApiTestContainersConfiguration.class).run(args);
    }
}
