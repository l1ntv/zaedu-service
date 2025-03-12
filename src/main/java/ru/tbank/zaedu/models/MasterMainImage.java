package ru.tbank.zaedu.models;

import jakarta.persistence.*;

@Entity
@Table(name = "master_main_images")
public class MasterMainImage {
    @Id
    @Column(name = "main_image_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "master_id")
    private MasterProfile master;

    private String url;

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
