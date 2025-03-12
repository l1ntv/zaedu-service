package ru.tbank.zaedu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "master_portfolio_images")
public class MasterPortfolioImage {
    @Id
    @Column(name = "portfolio_image_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "master_id")
    private MasterProfile master;

    private String url;

    // Конструктор
    public MasterPortfolioImage() {}

    public MasterPortfolioImage(MasterProfile master, String url) {
        this.master = master;
        this.url = url;
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
