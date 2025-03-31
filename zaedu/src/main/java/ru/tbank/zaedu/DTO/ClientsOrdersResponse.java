package ru.tbank.zaedu.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientsOrdersResponse {
    List<PlacedOrdersByClientsResponse> placedOrdersByClients;
    private String photoUrl;
    private int balance;
}
