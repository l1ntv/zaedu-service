package ru.tbank.zaedu.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterProfileForMeDTO {
    private String surname;
    private String name;
    private String patronymic;
    private String email;
    private String telephoneNumber;
    private Boolean isCompany;
    private Boolean isConfirmedPassport;
    private String passportSeries;
    private String passportNumber;
}
