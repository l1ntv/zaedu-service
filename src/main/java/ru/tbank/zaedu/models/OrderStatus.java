package ru.tbank.zaedu.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "order_statuses")
public class OrderStatus {
    @Id
    @Column(name = "status_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "status")
    private List<Order> orders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
