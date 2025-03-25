package ru.tbank.zaedu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import ru.tbank.zaedu.DTO.PlacedOrdersByClientsResponse;
import ru.tbank.zaedu.DTO.ServiceDTO;
import ru.tbank.zaedu.exceptionhandler.ResourceNotFoundException;
import ru.tbank.zaedu.models.*;
import ru.tbank.zaedu.repo.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final OrderStatusRepository orderStatusRepository;

    private final MasterMainImageRepository masterMainImageRepository;

    private static final String PLACED_ORDER_STATUS_CONST = "PLACED";

    private static final int DEFAULT_BALANCE_CONST = 500;

    @Override
    public ClientsOrdersResponse findPlacedOrdersByClients(String masterLogin) {
        OrderStatus placedOrderStatus = orderStatusRepository.findByName(PLACED_ORDER_STATUS_CONST).orElseThrow(() -> new ResourceNotFoundException("OrderStatusNotFound"));
        List<Order> placedOrders = orderRepository.findByStatus(placedOrderStatus);
        List<PlacedOrdersByClientsResponse> placedOrdersByClientsResponses = placedOrders.stream()
                .map(order -> {
                    PlacedOrdersByClientsResponse response = new PlacedOrdersByClientsResponse();
                    response.setId(order.getId());
                    response.setServiceDTO(
                            new ServiceDTO(
                                    order.getServices().getId(),
                                    order.getServices().getName(),
                                    order.getPrice())
                    );
                    response.setClientName(order.getClient().getName());
                    response.setAddress(order.getAddress());
                    response.setPrice(order.getPrice());
                    response.setDateFrom(order.getDateFrom());
                    response.setDateTo(order.getDateTo());
                    return response;
                })
                .collect(Collectors.toList());

        User user = userRepository.findByLogin(masterLogin).orElseThrow(() -> new ResourceNotFoundException("UserNotFound"));

        Optional<MasterMainImage> masterMainImage = masterMainImageRepository.findByMasterId(user.getId());

        String imageUrl = masterMainImage.map(MasterMainImage::getUrl).orElse(null);

        return new ClientsOrdersResponse(placedOrdersByClientsResponses, imageUrl, OrderServiceImpl.DEFAULT_BALANCE_CONST);
    }

    @Override
    public void getPlacedOrder(Long id, String masterLogin) {
        // Проверяем, не успел ли кто-то еще принять заказ

    }
}
