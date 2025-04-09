package ru.tbank.zaedu.service;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import ru.tbank.zaedu.DTO.CreatedOrderRequest;
import ru.tbank.zaedu.DTO.PlacedOrdersByClientsResponse;
import ru.tbank.zaedu.enums.OrderStatusEnum;
import ru.tbank.zaedu.exceptionhandler.ConflictResourceException;
import ru.tbank.zaedu.exceptionhandler.ResourceNotFoundException;
import ru.tbank.zaedu.models.*;
import ru.tbank.zaedu.repo.*;

import static ru.tbank.zaedu.config.AppConstants.BASE_IMAGE_URL;

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

    private final FinanceBalanceRepository financeBalanceRepository;

    private final OrderDeletionService orderDeletionService;

    @Override
    public ClientsOrdersResponse findPlacedOrdersByClients(String masterLogin) {
        User user = this.findUserByLogin(masterLogin);
        MasterProfile masterProfile = this.findMasterProfileByUserId(user.getId());
        List<Services> services = this.getMasterServices(masterProfile);
        OrderStatus placedOrderStatus = this.findOrderStatusByName(OrderStatusEnum.PLACED.toString());

        List<Order> placedOrders = orderRepository.findByStatusAndServicesIn(placedOrderStatus, services);

        List<PlacedOrdersByClientsResponse> placedOrdersByClientsResponses = placedOrders.stream()
                .map(order -> {
                    PlacedOrdersByClientsResponse response = new PlacedOrdersByClientsResponse();
                    response.setId(order.getId());
                    response.setServiceType(order.getServices().getName());
                    response.setNameClient(order.getClient().getName() != null ? order.getClient().getName() : "Клиент " + order.getClient().getUser().getId());
                    response.setAddress(order.getAddress());
                    response.setPrice(order.getPrice());
                    response.setDateFrom(order.getDateFrom());
                    response.setDateTo(order.getDateTo());
                    response.setDescription(order.getDescription());
                    return response;
                })
                .collect(Collectors.toList());

        Optional<MasterMainImage> masterMainImage = masterMainImageRepository.findByMasterId(user.getId());
        FinanceBalance financeBalance = this.findFinanceBalanceByUserId(user.getId());

        String imageUrl = masterMainImage.map(MasterMainImage::getFilename)
                .map(filename -> BASE_IMAGE_URL + filename)
                .orElse(null);
        return new ClientsOrdersResponse(
                placedOrdersByClientsResponses, imageUrl, financeBalance.getBalance());
    }

    @Transactional
    @Override
    public void assignOrderToMaster(Long id, String masterLogin) {
        Order order = this.findOrderById(id);

        if (!this.isOrderHasPlacedStatus(order)) {
            throw new ConflictResourceException("OrderAlreadyPickedUp");
        }

        FinanceBalance clientFinanceBalance = this.findFinanceBalanceByUserId(order.getClient().getUser().getId());
        if (!this.isEnoughMoney(clientFinanceBalance.getBalance(), order.getPrice())) {
            orderDeletionService.deleteOrderInNewTransaction(order);
            throw new ConflictResourceException("NotEnoughMoney");
        }
        clientFinanceBalance.setBalance(this.calculateNewBalanceAfterDebit(clientFinanceBalance.getBalance(), order.getPrice()));
        financeBalanceRepository.save(clientFinanceBalance);

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

        FinanceBalance financeBalance = this.findFinanceBalanceByUserId(user.getId());
        Long orderPrice = request.getPrice();
        Long clientBalance = financeBalance.getBalance();

        if (!this.isEnoughMoney(clientBalance, orderPrice)) {
            throw new ConflictResourceException("NotEnoughMoney");
        }
        financeBalance.setBalance(this.calculateNewBalanceAfterDebit(clientBalance, orderPrice));
        financeBalanceRepository.save(financeBalance);

        ClientProfile clientProfile = this.findClientProfileByUserId(user.getId());
        Services service = this.findServiceByName(request.getServiceType());
        OrderStatus placedOrderStatus = this.findOrderStatusByName(OrderStatusEnum.PLACED.toString());

        List<Order> listPossibleDuplicates = this.findPossibleDuplicates(clientProfile,
                service,
                request.getAddress(),
                request.getDateFrom(),
                request.getDateTo());

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

        FinanceBalance financeBalance = this.findFinanceBalanceByUserId(order.getMaster().getUser().getId());
        financeBalance.setBalance(financeBalance.getBalance() + this.calculateMasterRevenue(order.getPrice()));
        financeBalanceRepository.save(financeBalance);

        order.setStatus(completedOrderStatus);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void offerOrder(Long masterId, CreatedOrderRequest request, String clientLogin) {
        User user = this.findUserByLogin(clientLogin);
        ClientProfile clientProfile = this.findClientProfileByUserId(user.getId());
        MasterProfile masterProfile = this.findMasterProfileByUserId(masterId);
        Services service = this.findServiceByName(request.getServiceType());
        OrderStatus placedOrderStatus = this.findOrderStatusByName(OrderStatusEnum.PENDING.toString());

        List<Order> listPossibleDuplicates = this.findPossibleDuplicates(
                clientProfile, service, request.getAddress(), request.getDateFrom(), request.getDateTo());

        if (!listPossibleDuplicates.isEmpty()) {
            throw new ConflictResourceException("DuplicateOrder");
        }

        FinanceBalance clientFinanceBalance = this.findFinanceBalanceByUserId(user.getId());
        if (!this.isEnoughMoney(clientFinanceBalance.getBalance(), request.getPrice())) {
            throw new ConflictResourceException("NotEnoughMoney");
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

        FinanceBalance financeBalanceClient = this.findFinanceBalanceByUserId(
                order.getClient().getUser().getId());
        if (!this.isEnoughMoney(financeBalanceClient.getBalance(), order.getPrice())) {
            orderDeletionService.deleteOrderInNewTransaction(order);
            throw new ConflictResourceException("NotEnoughMoney");
        }
        financeBalanceClient.setBalance(this.calculateNewBalanceAfterDebit(
                financeBalanceClient.getBalance(),
                order.getPrice()));
        financeBalanceRepository.save(financeBalanceClient);

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

        if (!this.isOrderBelongToMaster(order.getMaster().getId(), masterProfile.getId())) {
            throw new ResourceNotFoundException("OrderNotBelongToMaster");
        }

        OrderStatus orderStatus = this.findOrderStatusByName(OrderStatusEnum.DECLINED.toString());
        order.setStatus(orderStatus);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void cancelOrder(Long id, String masterLogin) {
        Order order = this.findOrderById(id);
        User user = this.findUserByLogin(masterLogin);
        ClientProfile clientProfile = this.findClientProfileByUserId(user.getId());

        if (!order.getClient().getId().equals(clientProfile.getId())) {
            throw new ConflictResourceException("OrderNotBelongToClient");
        }

        if (order.getMaster() != null) {
            throw new ResourceNotFoundException("OrderAlreadyBelongToMaster");
        }

        FinanceBalance financeBalance = this.findFinanceBalanceByUserId(user.getId());
        financeBalance.setBalance(financeBalance.getBalance() + order.getPrice());
        financeBalanceRepository.save(financeBalance);

        orderDeletionService.deleteOrderInNewTransaction(order);
    }

    @Override
    public List<Order> getClientOrders(String name) {
        Optional<User> user = userRepository.findByLogin(name);

        Optional<ClientProfile> clientProfile = clientProfileRepository.findByUser_Id(user.get().getId());

        List<Order> orders = orderRepository.findByClient(clientProfile.get());

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
        MasterProfile masterProfile = masterProfileRepository.findByUser_Id(user.get().getId())
                .orElseThrow(() -> new ResourceNotFoundException("MasterProfileNotFound"));

        List<Order> orders = orderRepository.findByMaster(masterProfile);

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

    private List<Order> findPossibleDuplicates(ClientProfile clientProfile,
                                               Services service,
                                               String address,
                                               LocalDate dateFrom,
                                               LocalDate dateTo) throws ConflictResourceException {
        return orderRepository
                .findByClientAndServicesAndAddressAndDateFromLessThanEqualAndDateToGreaterThanEqual(clientProfile,
                        service,
                        address,
                        dateFrom,
                        dateTo);
    }

    private Order findOrderById(Long id) throws ResourceNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NotFoundOrder"));
    }

    private User findUserByLogin(String login) throws ResourceNotFoundException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("UserNotFound"));
    }

    private ClientProfile findClientProfileByUserId(Long id) throws ResourceNotFoundException {
        return clientProfileRepository.findByUser_Id(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientNotFound"));
    }

    private MasterProfile findMasterProfileByUserId(Long id) throws ResourceNotFoundException {
        return masterProfileRepository.findByUser_Id(id)
                .orElseThrow(() -> new ResourceNotFoundException("MasterNotFound"));
    }

    private OrderStatus findOrderStatusByName(String name) throws ResourceNotFoundException {
        return orderStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("OrderStatusNotFound"));
    }

    private Services findServiceByName(String name) throws ResourceNotFoundException {
        return serviceRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceNotFound"));
    }

    private FinanceBalance findFinanceBalanceByUserId(Long userId) throws ResourceNotFoundException {
        return financeBalanceRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("FinanceBalanceNotFound"));
    }

    private boolean isOrderHasPlacedStatus(Order order) {
        return order.getStatus().getName().equals(OrderStatusEnum.PLACED.toString());
    }

    private boolean isOrderBelongToMaster(Long masterIdFromOrder, Long masterIdFromProfile) {
        return masterIdFromOrder.equals(masterIdFromProfile);
    }

    private List<Services> getMasterServices(MasterProfile masterProfile) {
        return masterProfile.getServices().stream()
                .map(MasterServiceEntity::getServices)
                .toList();
    }

    private boolean isEnoughMoney(Long clientBalance, Long orderPrice) {
        return clientBalance >= orderPrice;
    }

    private Long calculateNewBalanceAfterDebit(Long currentBalance, Long orderPrice) {
        return currentBalance - orderPrice;
    }

    private Long calculateMasterRevenue(Long orderPrice) {
        return (orderPrice * 95 + 50) / 100;
    }
}
