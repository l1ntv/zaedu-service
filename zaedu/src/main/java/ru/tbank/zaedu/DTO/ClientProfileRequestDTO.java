package ru.tbank.zaedu.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class ClientProfileRequestDTO {
    private String surname;
    private String name;
    private String patronymic;
    private String email;
    private String telephoneNumber;
    private String city;
    private String filename;
    private UUID uuid;
}
