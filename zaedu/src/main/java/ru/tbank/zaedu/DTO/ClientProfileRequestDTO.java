package src.main.java.ru.tbank.zaedu.DTO;

import lombok.Data;

@Data
public class ClientProfileRequestDTO {
    private String surname;
    private String name;
    private String patronymic;
    private String email;
    private String telephoneNumber;
    private String city;
    private String mainImage;
}
