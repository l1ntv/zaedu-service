package ru.tbank.zaedu.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderDTO {
    private Long id;
    private Long clientId;
    private Long serviceId;
    private Long masterId;
    private String clientName;
    private String masterName;
    private String description;
    private String address;
    private Long price;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String orderStatus;
}
