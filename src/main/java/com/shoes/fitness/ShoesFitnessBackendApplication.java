package com.shoes.fitness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ShoesFitnessBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoesFitnessBackendApplication.class, args);
    }
}