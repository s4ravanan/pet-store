package com.dreamworks.petstore.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VirtualCustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(VirtualCustomerApplication.class, args);
    }
}
