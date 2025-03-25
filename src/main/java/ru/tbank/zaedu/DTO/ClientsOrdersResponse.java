package ru.tbank.zaedu.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ClientsOrdersResponse {
    List<PlacedOrdersByClientsResponse> placedOrdersByClients;
    private String photoUrl;
    private int balance;
}
