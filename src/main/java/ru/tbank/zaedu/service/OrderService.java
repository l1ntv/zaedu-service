package ru.tbank.zaedu.service;


import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import ru.tbank.zaedu.DTO.CreatedOrderRequest;

public interface OrderService {

    ClientsOrdersResponse findPlacedOrdersByClients(String masterLogin);

    void assignOrderToMaster(Long id, String masterLogin);

    void createOrder(CreatedOrderRequest request, String clientLogin);

    void closeOrder(Long id, String clientLogin);

    void offerOrder(Long masterId, CreatedOrderRequest request, String clientLogin);
}
