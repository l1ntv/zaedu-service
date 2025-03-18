package ru.tbank.zaedu.DTO;

import java.util.List;

public class MastersListResponseDTO {
    private List<MasterProfileDTO> masters;
    private String photoUrl;
    private Integer balance;

    public MastersListResponseDTO(List<MasterProfileDTO> masters, String photoUrl, Integer balance) {
        this.masters = masters;
        this.photoUrl = photoUrl;
        this.balance = balance;
    }

    public List<MasterProfileDTO> getMasters() {
        return masters;
    }

    public void setMasters(List<MasterProfileDTO> masters) {
        this.masters = masters;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }
}
