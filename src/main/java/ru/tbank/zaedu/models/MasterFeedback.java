package ru.tbank.zaedu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "master_feedback")
public class MasterFeedback {
    @Id
    @Column(name = "feedback_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "master_id")
    private MasterProfile master;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientProfile client;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer evaluation;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MasterProfile getMaster() {
        return master;
    }

    public void setMaster(MasterProfile master) {
        this.master = master;
    }

    public ClientProfile getClient() {
        return client;
    }

    public void setClient(ClientProfile client) {
        this.client = client;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Integer getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Integer evaluation) {
        this.evaluation = evaluation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
