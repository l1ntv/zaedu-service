package ru.tbank.zaedu.models;

import jakarta.persistence.*;
import ru.tbank.zaedu.service.MasterService;

import java.util.List;

@Entity
@Table(name = "services")
public class Services {
    @Id
    @Column(name = "service_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "service")
    private List<MasterService> masterServices;

    @OneToMany(mappedBy = "service")
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

    public List<MasterService> getMasterServices() {
        return masterServices;
    }

    public void setMasterServices(List<MasterService> masterServices) {
        this.masterServices = masterServices;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
