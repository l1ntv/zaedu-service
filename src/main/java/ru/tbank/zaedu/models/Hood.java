package ru.tbank.zaedu.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "hoods")
public class Hood {
    @Id
    @Column(name = "hood_id")
    private Long id;

    private String hoodName;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToMany(mappedBy = "hoods")
    private List<MasterProfile> masters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHoodName() {
        return hoodName;
    }

    public void setHoodName(String hoodName) {
        this.hoodName = hoodName;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<MasterProfile> getMasters() {
        return masters;
    }

    public void setMasters(List<MasterProfile> masters) {
        this.masters = masters;
    }
}
