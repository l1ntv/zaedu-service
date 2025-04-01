package ru.tbank.zaedu.service;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
                    response.setServiceDTO(new ServiceDTO(
                            order.getServices().getId(), order.getServices().getName(), order.getPrice()));
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
        return new ClientsOrdersResponse(
                placedOrdersByClientsResponses, imageUrl, OrderServiceImpl.DEFAULT_BALANCE_CONST);
    }

    @Transactional
    @Override
    public void assignOrderToMaster(Long id, String masterLogin) {
        Order order = this.findOrderById(id);

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

        List<Order> listPossibleDuplicates = this.findPossibleDuplicates(
                clientProfile, service, request.getAddress(), request.getDateFrom(), request.getDateTo());

        if (!listPossibleDuplicates.isEmpty()) {
            throw new ConflictResourceException("DuplicateOrder");
        }

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
        Order order = orderRepository
                .findByIdAndClient_Id(id, clientProfile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("OrderNotFound"));

        order.setStatus(completedOrderStatus);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void offerOrder(Long masterId, CreatedOrderRequest request, String clientLogin) {
        User user = this.findUserByLogin(clientLogin);

        System.out.println(user.getId());

        ClientProfile clientProfile = this.findClientProfileByUserId(user.getId());
        MasterProfile masterProfile = this.findMasterProfileByUserId(masterId);
        Services service = this.findServiceByName(request.getServiceName());
        OrderStatus placedOrderStatus = this.findOrderStatusByName(OrderStatusEnum.PENDING.toString());

        List<Order> listPossibleDuplicates = this.findPossibleDuplicates(
                clientProfile, service, request.getAddress(), request.getDateFrom(), request.getDateTo());

        if (!listPossibleDuplicates.isEmpty()) {
            throw new ConflictResourceException("DuplicateOrder");
        }

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

    @Transactional
    @Override
    public void acceptOrder(Long id, String masterLogin) {
        Order order = this.findOrderById(id);
        User user = this.findUserByLogin(masterLogin);
        MasterProfile masterProfile = this.findMasterProfileByUserId(user.getId());

        if (!order.getMaster().getId().equals(masterProfile.getId())) {
            throw new ConflictResourceException("OrderNotBelongToMaster");
        }

        OrderStatus orderStatus = this.findOrderStatusByName(OrderStatusEnum.IN_PROGRESS.toString());
        order.setStatus(orderStatus);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void declineOrder(Long id, String masterLogin) {
        Order order = this.findOrderById(id);
        User user = this.findUserByLogin(masterLogin);
        MasterProfile masterProfile = this.findMasterProfileByUserId(user.getId());

        if (!this.isOrderBelongToMaster(order.getId(), masterProfile.getId())) {
            throw new ResourceNotFoundException("OrderNotBelongToMaster");
        }

        OrderStatus orderStatus = this.findOrderStatusByName(OrderStatusEnum.DECLINED.toString());

        order.setStatus(orderStatus);
        orderRepository.save(order);
    }

    @Override
    public List<Order> getClientOrders(String name) {
        Optional<User> user = userRepository.findByLogin(name);
        List<Order> orders = orderRepository.findByClient_Id(user.get().getId());

        Map<String, Integer> statusOrder = Map.of(
                "IN_PROGRESS", 1,
                "PLACED", 2,
                "PENDING", 3,
                "COMPLETED", 4,
                "DECLINED", 5);

        orders.sort(Comparator.comparingInt(
                order -> statusOrder.getOrDefault(order.getStatus().getName(), Integer.MAX_VALUE)));

        return orders;
    }

    @Override
    public List<Order> getMasterOrders(String name) {
        Optional<User> user = userRepository.findByLogin(name);
        List<Order> orders = orderRepository.findByMaster_Id(user.get().getId());

        Map<String, Integer> statusOrder = Map.of(
                "IN_PROGRESS", 1,
                "PENDING", 2,
                "COMPLETED", 3);

        orders.sort(Comparator.comparingInt(
                order -> statusOrder.getOrDefault(order.getStatus().getName(), Integer.MAX_VALUE)));

        return orders;
    }

    private List<Order> findPossibleDuplicates(
            ClientProfile clientProfile, Services service, String address, LocalDate dateFrom, LocalDate dateTo)
            throws ConflictResourceException {
        List<Order> listPossibleDuplicates =
                orderRepository.findByClientAndServicesAndAddressAndDateFromLessThanEqualAndDateToGreaterThanEqual(
                        clientProfile, service, address, dateFrom, dateTo);
        return listPossibleDuplicates;
    }

    private Order findOrderById(Long id) throws ResourceNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("NotFoundOrder"));
        return order;
    }

    private User findUserByLogin(String login) throws ResourceNotFoundException {
        User user = userRepository.findByLogin(login).orElseThrow(() -> new ResourceNotFoundException("UserNotFound"));
        return user;
    }

    private ClientProfile findClientProfileByUserId(Long id) throws ResourceNotFoundException {
        ClientProfile clientProfile = clientProfileRepository.findByUser_Id(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientNotFound"));
        return clientProfile;
    }

    private MasterProfile findMasterProfileByUserId(Long id) throws ResourceNotFoundException {
        MasterProfile masterProfile = masterProfileRepository
                .findByUser_Id(id)
                .orElseThrow(() -> new ResourceNotFoundException("MasterNotFound"));
        return masterProfile;
    }

    private OrderStatus findOrderStatusByName(String name) throws ResourceNotFoundException {
        OrderStatus orderStatus = orderStatusRepository
                .findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("OrderStatusNotFound"));
        return orderStatus;
    }

    private Services findServiceByName(String name) throws ResourceNotFoundException {
        Services service =
                serviceRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("ServiceNotFound"));
        return service;
    }

    private boolean isOrderHasPlacedStatus(Order order) {
        return order.getStatus().getName().equals(OrderStatusEnum.PLACED.toString());
    }

    private boolean isOrderBelongToMaster(Long masterIdFromOrder, Long masterIdFromProfile) {
        return masterIdFromOrder.equals(masterIdFromProfile);
    }
}
