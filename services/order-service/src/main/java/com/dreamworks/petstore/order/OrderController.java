package com.dreamworks.petstore.order;

import java.time.Instant;
import java.util.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository repository;
    private final RabbitTemplate rabbit;

    public OrderController(OrderRepository repository, RabbitTemplate rabbit) {
        this.repository = repository;
        this.rabbit = rabbit;
    }

    @PostMapping
    public ResponseEntity<Order> place(@RequestBody CreateOrder request) {
        String id = UUID.randomUUID().toString();
        Order order = new Order(id, request.items(), request.customer(), "RECEIVED", Instant.now());
        Order saved = repository.save(order);
        rabbit.convertAndSend("petstore.orders", "order.created", saved);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(saved);
    }

    @GetMapping
    public List<Order> all() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Order one(@PathVariable String id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Order not found: " + id));
    }

    public record CreateOrder(List<String> items, String customer) {}
}
