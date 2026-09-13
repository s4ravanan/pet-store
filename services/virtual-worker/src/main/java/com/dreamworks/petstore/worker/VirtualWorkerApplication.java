package com.dreamworks.petstore.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VirtualWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(VirtualWorkerApplication.class, args);
    }
}
