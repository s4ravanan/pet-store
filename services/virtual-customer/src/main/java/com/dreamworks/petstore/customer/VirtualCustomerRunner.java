package com.dreamworks.petstore.customer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class VirtualCustomerRunner {
    private static final Logger log = LoggerFactory.getLogger(VirtualCustomerRunner.class);

    private final RestClient productClient;
    private final RestClient orderClient;
    private final boolean enabled;

    private static final List<String> CUSTOMER_NAMES =
            List.of(
                    "Virtual-Customer-Alex",
                    "Virtual-Customer-Bella",
                    "Virtual-Customer-Charlie",
                    "Virtual-Customer-Daisy",
                    "Virtual-Customer-Ethan",
                    "Virtual-Customer-Fiona");

    public VirtualCustomerRunner(
            @Value("${services.product-url:http://localhost:8081}") String productUrl,
            @Value("${services.order-url:http://localhost:8082}") String orderUrl,
            @Value("${simulation.enabled:true}") boolean enabled) {
        this.productClient = RestClient.create(productUrl);
        this.orderClient = RestClient.create(orderUrl);
        this.enabled = enabled;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Virtual Customer Simulator initialized (enabled={})", enabled);
    }

    @Scheduled(fixedDelayString = "${simulation.order-interval-ms:8000}", initialDelay = 5000)
    public void generateOrder() {
        if (!enabled) {
            return;
        }

        try {
            List<Map<String, Object>> products =
                    productClient
                            .get()
                            .uri("/api/products")
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

            if (products == null || products.isEmpty()) {
                log.warn("No products found from product-service; skipping order generation.");
                return;
            }

            int itemCount = ThreadLocalRandom.current().nextInt(1, 4);
            List<String> selectedItems = new ArrayList<>();
            for (int i = 0; i < itemCount; i++) {
                int index = ThreadLocalRandom.current().nextInt(products.size());
                selectedItems.add((String) products.get(index).get("id"));
            }

            String customer =
                    CUSTOMER_NAMES.get(ThreadLocalRandom.current().nextInt(CUSTOMER_NAMES.size()));

            Map<String, Object> orderRequest = Map.of("customer", customer, "items", selectedItems);

            Map<?, ?> response =
                    orderClient
                            .post()
                            .uri("/api/orders")
                            .body(orderRequest)
                            .retrieve()
                            .body(Map.class);

            log.info(
                    "Placed automated order: id={} customer={} items={}",
                    response != null ? response.get("id") : "unknown",
                    customer,
                    selectedItems);
        } catch (Exception e) {
            log.warn("Virtual customer failed to generate order: {}", e.getMessage());
        }
    }
}
