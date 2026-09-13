package com.dreamworks.petstore.product;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final ProductRepository repository;

    public DataInitializer(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            repository.saveAll(
                    List.of(
                            new Product(
                                    "dog-bed",
                                    "Cozy Dog Bed",
                                    "A soft bed for happy naps",
                                    29.99,
                                    "beds"),
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
                                    "toys")));
        }
    }
}
