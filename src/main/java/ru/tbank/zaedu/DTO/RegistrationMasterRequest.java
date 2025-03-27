package ru.tbank.zaedu.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private List<ServiceDTO> services;

    private Boolean isCompany;

    private String profileName;
}
