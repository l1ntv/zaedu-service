package ru.tbank.zaedu.service;


import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import ru.tbank.zaedu.models.Order;

import java.util.List;

public interface OrderService {

    ClientsOrdersResponse findPlacedOrdersByClients(String masterLogin);

    void getPlacedOrder(Long id, String masterLogin);

    List<Order> getClientOrders(String name);
}
