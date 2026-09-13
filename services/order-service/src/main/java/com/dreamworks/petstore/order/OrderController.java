package com.dreamworks.petstore.order;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final RabbitTemplate rabbit;

    public OrderController(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @PostMapping
    public ResponseEntity<Order> place(@RequestBody CreateOrder request) {
        String id = UUID.randomUUID().toString();
        Order order = new Order(id, request.items(), request.customer(), "RECEIVED", Instant.now());
        orders.put(id, order);
        rabbit.convertAndSend("petstore.orders", "order.created", order);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(order);
    }

    @GetMapping
    public Collection<Order> all() {
        return orders.values();
    }

    @GetMapping("/{id}")
    public Order one(@PathVariable String id) {
        return Optional.ofNullable(orders.get(id)).orElseThrow();
    }

    public record CreateOrder(List<String> items, String customer) {}

    public record Order(
            String id, List<String> items, String customer, String status, Instant createdAt) {}
}
