package ru.tbank.zaedu.DTO;

import lombok.ToString;

import java.util.List;
@ToString
public class MasterProfileDTO {
    private Integer id;
    private String fullName;
    private String description;
    private Boolean onlineStatus;
    private List<String> photos; // URLs of photos
    private String personalPhoto; // URL of personal photo
    private Double averageRating;
    private Integer ratingCount;
    private Boolean passportVerified;
    private Boolean contractWork;
    private List<ServiceDTO> services;
    private List<String> districts;
    private List<ReportDTO> reports;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getPersonalPhoto() {
        return personalPhoto;
    }

    public void setPersonalPhoto(String personalPhoto) {
        this.personalPhoto = personalPhoto;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Boolean getPassportVerified() {
        return passportVerified;
    }

    public void setPassportVerified(Boolean passportVerified) {
        this.passportVerified = passportVerified;
    }

    public Boolean getContractWork() {
        return contractWork;
    }

    public void setContractWork(Boolean contractWork) {
        this.contractWork = contractWork;
    }

    public List<ServiceDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceDTO> services) {
        this.services = services;
    }

    public List<String> getDistricts() {
        return districts;
    }

    public void setDistricts(List<String> districts) {
        this.districts = districts;
    }

    public List<ReportDTO> getReports() {
        return reports;
    }

    public void setReports(List<ReportDTO> reports) {
        this.reports = reports;
    }

}
