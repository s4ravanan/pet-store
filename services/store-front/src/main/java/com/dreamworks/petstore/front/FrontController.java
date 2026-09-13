package com.dreamworks.petstore.front;

import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api")
public class FrontController {
    private final RestClient products, orders;

    public FrontController(
            @Value("${PRODUCT_SERVICE_URL:http://localhost:8081}") String p,
            @Value("${ORDER_SERVICE_URL:http://localhost:8082}") String o) {
        products = RestClient.create(p);
        orders = RestClient.create(o);
    }

    @GetMapping("/products")
    Object products() {
        return products.get().uri("/api/products").retrieve().body(Object.class);
    }

    @PostMapping("/orders")
    Object order(@RequestBody Map<String, Object> body) {
        return orders.post().uri("/api/orders").body(body).retrieve().body(Object.class);
    }
}
