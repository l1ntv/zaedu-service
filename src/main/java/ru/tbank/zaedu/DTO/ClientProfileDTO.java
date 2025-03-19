package ru.tbank.zaedu.DTO;

import lombok.Data;

@Data
public class ClientProfileDTO {
    private Long id;
    private String name;
    private String surname;
    private String patronymic;
    private String email;
    private String telephoneNumber;
    private Long cityId;
    private Long userId;
    private String cityName;
}
