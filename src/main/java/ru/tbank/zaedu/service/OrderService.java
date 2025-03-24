package ru.tbank.zaedu.service;


import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import java.security.Principal;

public interface OrderService {

    ClientsOrdersResponse findPlacedOrdersByClients(String masterLogin);

    void getPlacedOrder(Long id, String masterLogin);
}
