package com.dreamworks.petstore.product;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final List<Product> products =
            List.of(
                    new Product(
                            "dog-bed", "Cozy Dog Bed", "A soft bed for happy naps", 29.99, "beds"),
                    new Product(
                            "cat-tower",
                            "Cat Adventure Tower",
                            "Climb, scratch and explore",
                            49.99,
                            "furniture"),
                    new Product(
                            "pet-food",
                            "Premium Pet Food",
                            "Healthy everyday nutrition",
                            19.99,
                            "food"),
                    new Product(
                            "chew-toy",
                            "Rainbow Chew Toy",
                            "A durable toy for active pets",
                            12.49,
                            "toys"));

    @GetMapping
    public List<Product> all() {
        return products;
    }

    @GetMapping("/{id}")
    public Product one(@PathVariable String id) {
        return products.stream().filter(p -> p.id().equals(id)).findFirst().orElseThrow();
    }

    public record Product(
            String id, String name, String description, BigDecimal price, String category) {
        Product(String i, String n, String d, double p, String c) {
            this(i, n, d, BigDecimal.valueOf(p), c);
        }
    }
}
