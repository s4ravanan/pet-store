package com.dreamworks.petstore.worker;

import java.util.*;
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
public class VirtualWorkerRunner {
    private static final Logger log = LoggerFactory.getLogger(VirtualWorkerRunner.class);

    private final RestClient makelineClient;
    private final boolean enabled;

    public VirtualWorkerRunner(
            @Value("${services.makeline-url:http://localhost:8083}") String makelineUrl,
            @Value("${simulation.enabled:true}") boolean enabled) {
        this.makelineClient = RestClient.create(makelineUrl);
        this.enabled = enabled;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Virtual Worker Simulator initialized (enabled={})", enabled);
    }

    @Scheduled(fixedDelayString = "${simulation.worker-interval-ms:10000}", initialDelay = 10000)
    public void processMakelineOrder() {
        if (!enabled) {
            return;
        }

        try {
            List<Map<String, Object>> orders =
                    makelineClient
                            .get()
                            .uri("/api/makeline/orders?status=IN_PROGRESS")
                            .retrieve()
                            .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

            if (orders == null || orders.isEmpty()) {
                return;
            }

            // Pick the first/oldest pending order to complete
            Map<String, Object> order = orders.get(0);
            String id = (String) order.get("id");
            if (id != null) {
                makelineClient
                        .post()
                        .uri("/api/makeline/orders/{id}/complete", id)
                        .retrieve()
                        .toBodilessEntity();
                log.info("Fulfilled and completed order: id={}", id);
            }
        } catch (Exception e) {
            log.warn("Virtual worker encountered error: {}", e.getMessage());
        }
    }
}
