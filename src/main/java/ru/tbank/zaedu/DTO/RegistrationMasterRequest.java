package ru.tbank.zaedu.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tbank.zaedu.models.Services;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationMasterRequest {

    private String login;

    private String password;

    private String surname;

    private String name;

    private String patronymic;

    private List<Services> services;

    private Boolean isCompany;

    private String profileName;
}
