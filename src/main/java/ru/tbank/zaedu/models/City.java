package ru.tbank.zaedu.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "cities")
public class City {
    @Id
    @Column(name = "city_id")
    private Long id;

    private String cityName;

    @OneToMany(mappedBy = "city")
    private List<ClientProfile> clients;

    @OneToMany(mappedBy = "city")
    private List<MasterProfile> masters;

    @OneToMany(mappedBy = "city")
    private List<Hood> hoods;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<ClientProfile> getClients() {
        return clients;
    }

    public void setClients(List<ClientProfile> clients) {
        this.clients = clients;
    }

    public List<MasterProfile> getMasters() {
        return masters;
    }

    public void setMasters(List<MasterProfile> masters) {
        this.masters = masters;
    }

    public List<Hood> getHoods() {
        return hoods;
    }

    public void setHoods(List<Hood> hoods) {
        this.hoods = hoods;
    }
}
