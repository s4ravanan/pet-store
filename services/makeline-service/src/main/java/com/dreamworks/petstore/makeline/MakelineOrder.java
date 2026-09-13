package com.dreamworks.petstore.makeline;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "makeline_orders")
public class MakelineOrder {
    @Id private String id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "makeline_order_items", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "item")
    private List<String> items = new ArrayList<>();

    private String customer;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public MakelineOrder() {}

    public MakelineOrder(
            String id,
            List<String> items,
            String customer,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.customer = customer;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
