package ru.tbank.zaedu.service;


import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import ru.tbank.zaedu.DTO.CreatedOrderRequest;
import ru.tbank.zaedu.models.Order;

import java.util.List;

public interface OrderService {

    ClientsOrdersResponse findPlacedOrdersByClients(String masterLogin);

    void assignOrderToMaster(Long id, String masterLogin);

    void createOrder(CreatedOrderRequest request, String clientLogin);

    void closeOrder(Long id, String clientLogin);

    void offerOrder(Long masterId, CreatedOrderRequest request, String clientLogin);

    List<Order> getClientOrders(String name);
}
