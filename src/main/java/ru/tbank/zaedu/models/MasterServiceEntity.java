package ru.tbank.zaedu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "master_services")
public class MasterServiceEntity {
    @EmbeddedId
    private MasterServiceId id;

    private Long price;

    @ManyToOne
    @MapsId("masterId")
    @JoinColumn(name = "master_id")
    private MasterProfile master;

    @ManyToOne
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    private Service service;

    public MasterServiceId getId() {
        return id;
    }

    public void setId(MasterServiceId id) {
        this.id = id;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public MasterProfile getMaster() {
        return master;
    }

    public void setMaster(MasterProfile master) {
        this.master = master;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
