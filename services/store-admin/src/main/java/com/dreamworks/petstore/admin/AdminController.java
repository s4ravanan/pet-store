package com.dreamworks.petstore.admin;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final RestClient productsClient;
    private final RestClient ordersClient;
    private final RestClient makelineClient;

    public AdminController(
            @Value("${PRODUCT_SERVICE_URL:http://localhost:8081}") String productUrl,
            @Value("${ORDER_SERVICE_URL:http://localhost:8082}") String orderUrl,
            @Value("${MAKELINE_SERVICE_URL:http://localhost:8083}") String makelineUrl) {
        this.productsClient = RestClient.create(productUrl);
        this.ordersClient = RestClient.create(orderUrl);
        this.makelineClient = RestClient.create(makelineUrl);
    }

    @GetMapping("/products")
    public Object listProducts() {
        return productsClient.get().uri("/api/products").retrieve().body(Object.class);
    }

    @PostMapping("/products")
    public Object createProduct(@RequestBody Map<String, Object> product) {
        return productsClient
                .post()
                .uri("/api/products")
                .body(product)
                .retrieve()
                .body(Object.class);
    }

    @PutMapping("/products/{id}")
    public Object updateProduct(@PathVariable String id, @RequestBody Map<String, Object> product) {
        return productsClient
                .put()
                .uri("/api/products/{id}", id)
                .body(product)
                .retrieve()
                .body(Object.class);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productsClient.delete().uri("/api/products/{id}", id).retrieve().toBodilessEntity();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders")
    public Object listOrders() {
        return ordersClient.get().uri("/api/orders").retrieve().body(Object.class);
    }

    @GetMapping("/makeline/orders")
    public Object listMakelineOrders(@RequestParam(required = false) String status) {
        String uri =
                status != null ? "/api/makeline/orders?status=" + status : "/api/makeline/orders";
        return makelineClient.get().uri(uri).retrieve().body(Object.class);
    }

    @PostMapping("/makeline/orders/{id}/complete")
    public Object completeMakelineOrder(@PathVariable String id) {
        return makelineClient
                .post()
                .uri("/api/makeline/orders/{id}/complete", id)
                .retrieve()
                .body(Object.class);
    }
}
