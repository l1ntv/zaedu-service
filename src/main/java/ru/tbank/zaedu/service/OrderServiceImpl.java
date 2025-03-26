package ru.tbank.zaedu.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import ru.tbank.zaedu.DTO.CreatedOrderRequest;
import ru.tbank.zaedu.DTO.PlacedOrdersByClientsResponse;
import ru.tbank.zaedu.DTO.ServiceDTO;
import ru.tbank.zaedu.enums.OrderStatusEnum;
import ru.tbank.zaedu.exceptionhandler.ConflictResourceException;
import ru.tbank.zaedu.exceptionhandler.ResourceNotFoundException;
import ru.tbank.zaedu.models.*;
import ru.tbank.zaedu.repo.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final MasterProfileRepository masterProfileRepository;

    private final ClientProfileRepository clientProfileRepository;

    private final ServiceRepository serviceRepository;

    private final OrderStatusRepository orderStatusRepository;

    private final MasterMainImageRepository masterMainImageRepository;

    private static final int DEFAULT_BALANCE_CONST = 500;

    @Override
    public ClientsOrdersResponse findPlacedOrdersByClients(String masterLogin) {
        OrderStatus placedOrderStatus = this.findOrderStatusByName(OrderStatusEnum.PLACED.toString());
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

        User user = this.findUserByLogin(masterLogin);
        Optional<MasterMainImage> masterMainImage = masterMainImageRepository.findByMasterId(user.getId());
        String imageUrl = masterMainImage.map(MasterMainImage::getUrl).orElse(null);
        return new ClientsOrdersResponse(placedOrdersByClientsResponses, imageUrl, OrderServiceImpl.DEFAULT_BALANCE_CONST);
    }

    @Transactional
    @Override
    public void assignOrderToMaster(Long id, String masterLogin) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("OrderNotFound"));

        if (!this.isOrderHasPlacedStatus(order)) {
            throw new ConflictResourceException("OrderAlreadyPickedUp");
        }

        OrderStatus orderStatus = this.findOrderStatusByName(OrderStatusEnum.IN_PROGRESS.toString());
        User user = this.findUserByLogin(masterLogin);
        MasterProfile masterProfile = this.findMasterProfileByUserId(user.getId());

        order.setStatus(orderStatus);
        order.setMaster(masterProfile);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void createOrder(CreatedOrderRequest request, String clientLogin) {
        User user = this.findUserByLogin(clientLogin);
        ClientProfile clientProfile = this.findClientProfileByUserId(user.getId());
        Services service = this.findServiceByName(request.getServiceName());
        OrderStatus placedOrderStatus = this.findOrderStatusByName(OrderStatusEnum.PLACED.toString());

        Order order = Order.builder()
                .client(clientProfile)
                .services(service)
                .master(null)
                .price(request.getPrice())
                .address(request.getAddress())
                .status(placedOrderStatus)
                .dateFrom(request.getDateFrom())
                .dateTo(request.getDateTo())
                .description(request.getDescription())
                .build();
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void closeOrder(Long id, String clientLogin) {
        User user = this.findUserByLogin(clientLogin);
        OrderStatus completedOrderStatus = this.findOrderStatusByName(OrderStatusEnum.COMPLETED.toString());
        ClientProfile clientProfile = this.findClientProfileByUserId(user.getId());
        Order order = orderRepository.findByIdAndClient_Id(id, clientProfile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("OrderNotFound"));

        order.setStatus(completedOrderStatus);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void offerOrder(Long masterId, CreatedOrderRequest request, String clientLogin) {
        User user = this.findUserByLogin(clientLogin);
        ClientProfile clientProfile = this.findClientProfileByUserId(user.getId());
        MasterProfile masterProfile = this.findMasterProfileByUserId(masterId);
        Services service = this.findServiceByName(request.getServiceName());
        OrderStatus placedOrderStatus = this.findOrderStatusByName(OrderStatusEnum.PENDING.toString());

        Order order = Order.builder()
                .client(clientProfile)
                .master(masterProfile)
                .services(service)
                .status(placedOrderStatus)
                .description(request.getDescription())
                .dateFrom(request.getDateFrom())
                .dateTo(request.getDateTo())
                .address(request.getAddress())
                .price(request.getPrice())
                .build();
        orderRepository.save(order);
    }

    private User findUserByLogin(String login) throws ResourceNotFoundException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("UserNotFound"));
        return user;
    }

    private ClientProfile findClientProfileByUserId(Long id) throws ResourceNotFoundException {
        ClientProfile clientProfile = clientProfileRepository.findByUser_Id(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientNotFound"));
        return clientProfile;
    }

    private MasterProfile findMasterProfileByUserId(Long id) throws ResourceNotFoundException {
        MasterProfile masterProfile = masterProfileRepository.findByUser_Id(id)
                .orElseThrow(() -> new ResourceNotFoundException("MasterNotFound"));
        return masterProfile;
    }

    private OrderStatus findOrderStatusByName(String name) throws ResourceNotFoundException {
        OrderStatus orderStatus = orderStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("OrderStatusNotFound"));
        return orderStatus;
    }

    private Services findServiceByName(String name) throws ResourceNotFoundException {
        Services service = serviceRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceNotFound"));
        return service;
    }

    private boolean isOrderHasPlacedStatus(Order order) {
        return order.getStatus().getName().equals(OrderStatusEnum.PLACED.toString());
    }
}
