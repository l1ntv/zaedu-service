package ru.tbank.zaedu.service;


import ru.tbank.zaedu.DTO.PlacedOrdersByClientsResponse;

import java.util.List;

public interface OrderService {

    List<PlacedOrdersByClientsResponse> findPlacedOrdersByClients();
}
