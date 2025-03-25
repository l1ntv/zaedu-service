package ru.tbank.zaedu.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String serviceType;
    private String description;
    private String address;
    private Long price;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String phoneMaster;
}
