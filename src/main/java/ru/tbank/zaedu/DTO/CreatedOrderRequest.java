package ru.tbank.zaedu.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CreatedOrderRequest {
    private String serviceName;
    private String description;
    private Long price;
    private String address;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String phone;
}
