package ru.tbank.zaedu.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceDTO {
    private Long id;
    private String serviceName;
    private Long cost;

    @JsonCreator
    public ServiceDTO(
            @JsonProperty("serviceName") String serviceName,
            @JsonProperty("cost") Long cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public ServiceDTO(long i, String serviceName) {
        this.id = i;
        this.serviceName = serviceName;
        this.cost = null;
    }

    public ServiceDTO(Long id, String serviceName, Long cost) {
        this.id = id;
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String name) {
        this.serviceName = name;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }
}
