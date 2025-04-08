package ru.tbank.zaedu.DTO;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PlacedOrdersByClientsResponse {
    private Long id;
    private String serviceType;
    private String nameClient;
    private String description;
    private String address;
    private Long price;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
