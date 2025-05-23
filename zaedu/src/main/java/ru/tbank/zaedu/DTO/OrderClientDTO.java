package ru.tbank.zaedu.DTO;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderClientDTO {
    private Long id;
    private String status;
    private String serviceType;
    private String description;
    private String address;
    private Long price;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String nameMaster;
    private String phoneMaster;
}
