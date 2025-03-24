package ru.tbank.zaedu.service;


import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import java.security.Principal;

public interface OrderService {

    ClientsOrdersResponse findPlacedOrdersByClients(Principal principal);
}
