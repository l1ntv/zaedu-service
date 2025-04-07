package ru.tbank.zaedu.service;

import java.util.List;

import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import ru.tbank.zaedu.DTO.CreatedOrderRequest;
import ru.tbank.zaedu.models.Order;

public interface OrderService {

    ClientsOrdersResponse findPlacedOrdersByClients(String masterLogin);

    void assignOrderToMaster(Long id, String masterLogin);

    void createOrder(CreatedOrderRequest request, String clientLogin);

    void closeOrder(Long id, String clientLogin);

    void offerOrder(Long masterId, CreatedOrderRequest request, String clientLogin);

    List<Order> getClientOrders(String name);

    void acceptOrder(Long id, String masterLogin);

    void declineOrder(Long id, String masterLogin);

    void cancelOrder(Long id, String masterLogin);

    List<Order> getMasterOrders(String name);
}
