package ru.tbank.zaedu.DTO;

import lombok.Data;

@Data
public class ClientProfileResponseDTO {
    private String surname;
    private String name;
    private String patronymic;
    private String email;
    private String telephoneNumber;
    private String city;
    private String photoUrl;
    private Long balance;
}
