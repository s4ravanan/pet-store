package com.dreamworks.petstore.makeline;

import java.time.Instant;
import java.util.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/makeline/orders")
public class MakelineController {
    private final MakelineOrderRepository repository;

    public MakelineController(MakelineOrderRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "petstore.makeline")
    public void consume(Map<String, Object> orderData) {
        String id = (String) orderData.get("id");
        if (id == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<String> items = (List<String>) orderData.getOrDefault("items", List.of());
        String customer = (String) orderData.getOrDefault("customer", "Anonymous");

        MakelineOrder makelineOrder =
                new MakelineOrder(id, items, customer, "IN_PROGRESS", Instant.now(), Instant.now());
        repository.save(makelineOrder);
    }

    @GetMapping
    public List<MakelineOrder> all(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return repository.findByStatus(status.toUpperCase());
        }
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public MakelineOrder one(@PathVariable String id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Makeline order not found: " + id));
    }

    @PutMapping("/{id}")
    public MakelineOrder updateStatus(
            @PathVariable String id, @RequestBody Map<String, String> body) {
        return repository
                .findById(id)
                .map(
                        order -> {
                            String newStatus = body.getOrDefault("status", order.getStatus());
                            order.setStatus(newStatus.toUpperCase());
                            order.setUpdatedAt(Instant.now());
                            return repository.save(order);
                        })
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Makeline order not found: " + id));
    }

    @PostMapping("/{id}/complete")
    public MakelineOrder complete(@PathVariable String id) {
        return repository
                .findById(id)
                .map(
                        order -> {
                            order.setStatus("COMPLETED");
                            order.setUpdatedAt(Instant.now());
                            return repository.save(order);
                        })
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Makeline order not found: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Makeline order not found: " + id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
