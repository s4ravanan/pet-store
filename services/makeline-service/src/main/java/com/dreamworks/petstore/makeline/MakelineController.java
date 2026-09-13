package com.dreamworks.petstore.makeline;

import java.util.*;
import java.util.concurrent.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/makeline/orders")
public class MakelineController {
    private final Map<String, Map<String, Object>> orders = new ConcurrentHashMap<>();

    @RabbitListener(queues = "petstore.makeline")
    public void consume(Map<String, Object> order) {
        String id = (String) order.get("id");
        orders.put(
                id, new LinkedHashMap<>(Map.of("id", id, "status", "IN_PROGRESS", "order", order)));
    }

    @GetMapping
    public Collection<Map<String, Object>> all() {
        return orders.values();
    }
}
