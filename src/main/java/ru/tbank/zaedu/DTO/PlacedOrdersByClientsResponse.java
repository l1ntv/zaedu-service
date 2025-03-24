package ru.tbank.zaedu.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PlacedOrdersByClientsResponse {
    private Long id;
    private ServiceDTO serviceDTO;
    private String clientName;
    private String address;
    private Long price;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
