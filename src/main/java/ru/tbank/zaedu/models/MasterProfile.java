package ru.tbank.zaedu.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "master_profile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MasterProfile {
    @Id
    @Column(name = "master_id")
    private Long id;

    private String surname;
    private String name;
    private String patronymic;
    private String email;
    private String telephoneNumber;
    private Boolean isCompany;
    private Boolean isConfirmedPassport;
    private String passportSeries;
    private String passportNumber;
    private LocalDate createDate;
    private String profileName;
    private String description;
    private Boolean isOnline;
    private Boolean isWorkingWithContract;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @OneToOne
    @MapsId
    @JoinColumn(name = "master_id")
    private User user;

    @OneToMany(mappedBy = "master")
    private List<MasterServiceEntity> services;

    @OneToMany(mappedBy = "master")
    private List<MasterFeedback> feedbacks;

    @ManyToMany
    @JoinTable(
            name = "master_hoods",
            joinColumns = @JoinColumn(name = "master_id"),
            inverseJoinColumns = @JoinColumn(name = "hood_id")
    )
    private List<Hood> hoods = new ArrayList<>();

    @OneToMany(mappedBy = "master")
    private List<Order> orders;

    @OneToMany(mappedBy = "master")
    private List<MasterMainImage> mainImages;

    @OneToMany(mappedBy = "master", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MasterPortfolioImage> portfolioImages = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public Boolean getCompany() {
        return isCompany;
    }

    public void setCompany(Boolean company) {
        isCompany = company;
    }

    public Boolean getConfirmedPassport() {
        return isConfirmedPassport;
    }

    public void setConfirmedPassport(Boolean confirmedPassport) {
        isConfirmedPassport = confirmedPassport;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public Boolean getWorkingWithContract() {
        return isWorkingWithContract;
    }

    public void setWorkingWithContract(Boolean workingWithContract) {
        isWorkingWithContract = workingWithContract;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<MasterServiceEntity> getServices() {
        return services;
    }

    public void setServices(List<MasterServiceEntity> services) {
        this.services = services;
    }

    public List<MasterFeedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<MasterFeedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public List<Hood> getHoods() {
        return hoods;
    }

    public void setHoods(List<Hood> hoods) {
        this.hoods = hoods;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<MasterMainImage> getMainImages() {
        return mainImages;
    }

    public void setMainImages(List<MasterMainImage> mainImages) {
        this.mainImages = mainImages;
    }

    public List<MasterPortfolioImage> getPortfolioImages() {
        return portfolioImages;
    }

    public void setPortfolioImages(List<MasterPortfolioImage> portfolioImages) {
        this.portfolioImages = portfolioImages;
    }
}
