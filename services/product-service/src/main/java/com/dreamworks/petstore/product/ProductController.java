package com.dreamworks.petstore.product;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Product> all() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Product one(@PathVariable String id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Product not found: " + id));
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        if (product.getId() == null || product.getId().isBlank()) {
            product.setId(
                    product.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-")
                            + "-"
                            + System.currentTimeMillis());
        }
        Product saved = repository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable String id, @RequestBody Product product) {
        return repository
                .findById(id)
                .map(
                        existing -> {
                            existing.setName(product.getName());
                            existing.setDescription(product.getDescription());
                            existing.setPrice(product.getPrice());
                            existing.setCategory(product.getCategory());
                            return repository.save(existing);
                        })
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Product not found: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
