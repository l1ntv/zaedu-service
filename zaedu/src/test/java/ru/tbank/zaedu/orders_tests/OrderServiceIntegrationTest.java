package ru.tbank.zaedu.orders_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tbank.zaedu.AbstractIntegrationTest;
import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import ru.tbank.zaedu.DTO.CreatedOrderRequest;
import ru.tbank.zaedu.enums.OrderStatusEnum;
import ru.tbank.zaedu.exceptionhandler.ConflictResourceException;
import ru.tbank.zaedu.exceptionhandler.ResourceNotFoundException;
import ru.tbank.zaedu.models.*;
import ru.tbank.zaedu.repo.*;
import ru.tbank.zaedu.service.OrderServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MasterProfileRepository masterProfileRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    private OrderRepository orderRepository;

    @Autowired
    private FinanceBalanceRepository financeBalanceRepository;

    @Autowired
    private MasterMainImageRepository masterMainImageRepository;

    @Autowired
    private ClientProfileRepository clientProfileRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private MasterServiceEntityRepository masterServiceEntityRepository;



    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        masterServiceEntityRepository.deleteAll();
        masterProfileRepository.deleteAll();
        clientProfileRepository.deleteAll();
        serviceRepository.deleteAll();
        financeBalanceRepository.deleteAll();
        masterMainImageRepository.deleteAll();
        orderStatusRepository.deleteAll();
        userRepository.deleteAll();

    }

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        User user = new User();
        user.setLogin("testclient");
        user.setPassword("password");
        userRepository.save(user);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(user);
        clientProfileRepository.save(clientProfile);

        Services service = new Services();
        service.setName("Test Service");
        serviceRepository.save(service);

        OrderStatus placedOrderStatus = new OrderStatus();
        placedOrderStatus.setName(OrderStatusEnum.PLACED.toString());
        orderStatusRepository.save(placedOrderStatus);

        FinanceBalance financeBalance = new FinanceBalance();
        financeBalance.setUser(user);
        financeBalance.setBalance(1000L); // Начальный баланс клиента
        financeBalanceRepository.save(financeBalance);

        CreatedOrderRequest request = new CreatedOrderRequest(
                "Test Service",
                "Test Description",
                200L,
                "Test Address",
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );

        // Act
        orderService.createOrder(request, "testclient");

        // Assert
        List<Order> orders = orderRepository.findByClient(clientProfile);
        assertEquals(1, orders.size());
        assertEquals("Test Service", orders.get(0).getServices().getName());
        assertEquals("Test Address", orders.get(0).getAddress());
        assertEquals(200L, orders.get(0).getPrice());

        FinanceBalance updatedFinanceBalance = financeBalanceRepository.findByUser_Id(user.getId()).orElseThrow();
        assertEquals(800L, updatedFinanceBalance.getBalance());
    }

    @Test
    void testFindPlacedOrdersByClients_Success() {
        // Arrange
        User user1 = new User();
        user1.setLogin("testmaster");
        user1.setPassword("password");
        userRepository.save(user1);

        User user2 = new User();
        user2.setLogin("testclient");
        user2.setPassword("password");
        userRepository.save(user2);

        MasterProfile masterProfile = new MasterProfile();
        masterProfile.setUser(user1);
        masterProfileRepository.save(masterProfile);

        Services service = new Services();
        service.setName("Test Service");
        serviceRepository.save(service);

        MasterServiceEntity masterService = new MasterServiceEntity();
        masterService.setMaster(masterProfile);
        masterService.setServices(service);
        masterService.setPrice(100L);
        masterServiceEntityRepository.save(masterService);

        masterProfile.getServices().add(masterService);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(user2);
        clientProfileRepository.save(clientProfile);

        OrderStatus placedStatus = new OrderStatus();
        placedStatus.setName(OrderStatusEnum.PLACED.toString());
        orderStatusRepository.save(placedStatus);

        Order order = new Order();
        order.setServices(service);
        order.setStatus(placedStatus);
        order.setClient(clientProfile);
        order.setAddress("Address");
        order.setDescription("Description");
        orderRepository.save(order);

        FinanceBalance financeBalance = new FinanceBalance();
        financeBalance.setUser(user1);
        financeBalance.setBalance(500L);
        financeBalanceRepository.save(financeBalance);

        MasterMainImage masterMainImage = new MasterMainImage();
        masterMainImage.setMaster(masterProfile);
        masterMainImage.setFilename("profile.jpg");
        masterMainImage.setUploadId(UUID.randomUUID());
        masterMainImageRepository.save(masterMainImage);

        // Act
        ClientsOrdersResponse response = orderService.findPlacedOrdersByClients("testmaster");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getPlacedOrdersByClients().size());
        assertEquals("Test Service", response.getPlacedOrdersByClients().get(0).getServiceType());
        assertEquals(500L, response.getBalance());
    }

    @Test
    void testAssignOrderToMaster_Success() {
        // Arrange
        // Создаем пользователя-мастера
        User masterUser = new User();
        masterUser.setLogin("testmaster");
        masterUser.setPassword("password");
        userRepository.save(masterUser);

        // Создаем пользователя-клиента
        User clientUser = new User();
        clientUser.setLogin("testclient");
        clientUser.setPassword("password");
        userRepository.save(clientUser);

        // Создаем профиль мастера
        MasterProfile masterProfile = new MasterProfile();
        masterProfile.setUser(masterUser);
        masterProfileRepository.save(masterProfile);

        // Создаем сервис
        Services service = new Services();
        service.setName("Test Service");
        serviceRepository.save(service);

        // Создаем связь мастера и сервиса
        MasterServiceEntity masterService = new MasterServiceEntity();
        masterService.setMaster(masterProfile);
        masterService.setServices(service);
        masterService.setPrice(100L);
        masterServiceEntityRepository.save(masterService);

        masterProfile.getServices().add(masterService);

        // Создаем профиль клиента
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(clientUser);
        clientProfileRepository.save(clientProfile);

        // Создаем статус "PLACED"
        OrderStatus placedStatus = new OrderStatus();
        placedStatus.setName(OrderStatusEnum.PLACED.toString());
        orderStatusRepository.save(placedStatus);

        // Создаем статус "IN_PROGRESS"
        OrderStatus inProgressStatus = new OrderStatus();
        inProgressStatus.setName(OrderStatusEnum.IN_PROGRESS.toString());
        orderStatusRepository.save(inProgressStatus);

        // Создаем заказ
        Order order = new Order();
        order.setServices(service);
        order.setStatus(placedStatus);
        order.setClient(clientProfile);
        order.setAddress("Address");
        order.setDescription("Description");
        order.setPrice(100L); // Цена заказа
        orderRepository.save(order);

        // Создаем баланс клиента
        FinanceBalance clientFinanceBalance = new FinanceBalance();
        clientFinanceBalance.setUser(clientUser);
        clientFinanceBalance.setBalance(500L); // Баланс клиента
        financeBalanceRepository.save(clientFinanceBalance);

        // Act
        orderService.assignOrderToMaster(order.getId(), "testmaster");

        // Assert
        // Проверяем, что статус заказа изменился на "IN_PROGRESS"
        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatusEnum.IN_PROGRESS.toString(), updatedOrder.getStatus().getName());

        // Проверяем, что мастер назначен к заказу
        assertNotNull(updatedOrder.getMaster());
        assertEquals(masterProfile.getId(), updatedOrder.getMaster().getId());

        // Проверяем, что баланс клиента обновлен
        FinanceBalance updatedClientFinanceBalance = financeBalanceRepository.findByUser_Id(clientUser.getId()).orElseThrow();
        assertEquals(400L, updatedClientFinanceBalance.getBalance()); // 500 - 100 = 400
    }

    @Test
    void testOfferOrder_Success() {
        // Arrange
        // Создаем пользователя-мастера
        User masterUser = new User();
        masterUser.setLogin("testmaster");
        masterUser.setPassword("password");
        userRepository.save(masterUser);

        MasterProfile masterProfile = new MasterProfile();
        masterProfile.setUser(masterUser);
        masterProfileRepository.save(masterProfile);

        // Создаем пользователя-клиента
        User clientUser = new User();
        clientUser.setLogin("testclient");
        clientUser.setPassword("password");
        userRepository.save(clientUser);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(clientUser);
        clientProfileRepository.save(clientProfile);

        // Создаем сервис
        Services service = new Services();
        service.setName("Test Service");
        serviceRepository.save(service);

        // Создаем статус "PENDING"
        OrderStatus pendingStatus = new OrderStatus();
        pendingStatus.setName(OrderStatusEnum.PENDING.toString());
        orderStatusRepository.save(pendingStatus);

        // Создаем баланс клиента
        FinanceBalance clientFinanceBalance = new FinanceBalance();
        clientFinanceBalance.setUser(clientUser);
        clientFinanceBalance.setBalance(500L); // Достаточный баланс
        financeBalanceRepository.save(clientFinanceBalance);

        // Создаем запрос на создание заказа
        CreatedOrderRequest request = new CreatedOrderRequest(
                "Test Service",
                "Test Description",
                100L,
                "Test Address",
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );

        // Act
        orderService.offerOrder(masterProfile.getUser().getId(), request, "testclient");

        // Assert
        // Проверяем, что заказ был создан
        List<Order> orders = orderRepository.findAll();
        assertEquals(1, orders.size());

        Order createdOrder = orders.get(0);
        assertEquals("Test Service", createdOrder.getServices().getName());
        assertEquals("Test Description", createdOrder.getDescription());
        assertEquals("Test Address", createdOrder.getAddress());
        assertEquals(OrderStatusEnum.PENDING.toString(), createdOrder.getStatus().getName());
        assertEquals(masterProfile.getId(), createdOrder.getMaster().getId());
        assertEquals(clientProfile.getId(), createdOrder.getClient().getId());
    }

    @Test
    void testOfferOrder_DuplicateOrder() {
        // Arrange
        // Создаем пользователя-мастера
        User masterUser = new User();
        masterUser.setLogin("testmaster");
        masterUser.setPassword("password");
        userRepository.save(masterUser);

        MasterProfile masterProfile = new MasterProfile();
        masterProfile.setUser(masterUser);
        masterProfileRepository.save(masterProfile);

        // Создаем пользователя-клиента
        User clientUser = new User();
        clientUser.setLogin("testclient");
        clientUser.setPassword("password");
        userRepository.save(clientUser);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(clientUser);
        clientProfileRepository.save(clientProfile);

        // Создаем сервис
        Services service = new Services();
        service.setName("Test Service");
        serviceRepository.save(service);

        // Создаем статус "PENDING"
        OrderStatus pendingStatus = new OrderStatus();
        pendingStatus.setName(OrderStatusEnum.PENDING.toString());
        orderStatusRepository.save(pendingStatus);

        // Создаем баланс клиента
        FinanceBalance clientFinanceBalance = new FinanceBalance();
        clientFinanceBalance.setUser(clientUser);
        clientFinanceBalance.setBalance(500L); // Достаточный баланс
        financeBalanceRepository.save(clientFinanceBalance);

        // Создаем первый заказ
        Order existingOrder = new Order();
        existingOrder.setClient(clientProfile);
        existingOrder.setMaster(masterProfile);
        existingOrder.setServices(service);
        existingOrder.setStatus(pendingStatus);
        existingOrder.setDescription("Test Description");
        existingOrder.setAddress("Test Address");
        existingOrder.setDateFrom(LocalDate.now().plusDays(1));
        existingOrder.setDateTo(LocalDate.now().plusDays(2));
        existingOrder.setPrice(100L);
        orderRepository.save(existingOrder);

        // Создаем запрос на создание дубликата заказа
        CreatedOrderRequest request = new CreatedOrderRequest(
                "Test Service",
                "Test Description",
                100L,
                "Test Address",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );

        // Act & Assert
        ConflictResourceException exception = assertThrows(ConflictResourceException.class, () -> {
            orderService.offerOrder(masterProfile.getUser().getId(), request, "testclient");
        });

        // Проверяем сообщение исключения
        assertEquals("DuplicateOrder", exception.getMessage());
    }

    @Test
    void testOfferOrder_NotEnoughMoney() {
        // Arrange
        // Создаем пользователя-мастера
        User masterUser = new User();
        masterUser.setLogin("testmaster");
        masterUser.setPassword("password");
        userRepository.save(masterUser);

        MasterProfile masterProfile = new MasterProfile();
        masterProfile.setUser(masterUser);
        masterProfileRepository.save(masterProfile);

        // Создаем пользователя-клиента
        User clientUser = new User();
        clientUser.setLogin("testclient");
        clientUser.setPassword("password");
        userRepository.save(clientUser);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(clientUser);
        clientProfileRepository.save(clientProfile);

        // Создаем сервис
        Services service = new Services();
        service.setName("Test Service");
        serviceRepository.save(service);

        // Создаем статус "PENDING"
        OrderStatus pendingStatus = new OrderStatus();
        pendingStatus.setName(OrderStatusEnum.PENDING.toString());
        orderStatusRepository.save(pendingStatus);

        // Создаем баланс клиента с недостаточным количеством средств
        FinanceBalance clientFinanceBalance = new FinanceBalance();
        clientFinanceBalance.setUser(clientUser);
        clientFinanceBalance.setBalance(50L); // Недостаточный баланс
        financeBalanceRepository.save(clientFinanceBalance);

        // Создаем запрос на создание заказа
        CreatedOrderRequest request = new CreatedOrderRequest(
                "Test Service",
                "Test Description",
                200L,
                "Test Address",
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );

        // Act & Assert
        ConflictResourceException exception = assertThrows(ConflictResourceException.class, () -> {
            orderService.offerOrder(masterProfile.getUser().getId(), request, "testclient");
        });

        // Проверяем сообщение исключения
        assertEquals("NotEnoughMoney", exception.getMessage());

        // Проверяем, что заказ не был создан
        assertTrue(orderRepository.findAll().isEmpty());
    }

    @Test
    void testAcceptOrder_Success() {
        // Arrange
        // Создаем пользователя-мастера
        User masterUser = new User();
        masterUser.setLogin("testmaster");
        userRepository.save(masterUser);

        MasterProfile masterProfile = new MasterProfile();
        masterProfile.setUser(masterUser);
        masterProfileRepository.save(masterProfile);

        // Создаем пользователя-клиента
        User clientUser = new User();
        clientUser.setLogin("testclient");
        userRepository.save(clientUser);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(clientUser);
        clientProfileRepository.save(clientProfile);

        // Создаем статусы
        OrderStatus pendingStatus = new OrderStatus();
        pendingStatus.setName(OrderStatusEnum.PENDING.toString());
        orderStatusRepository.save(pendingStatus);

        OrderStatus inProgressStatus = new OrderStatus();
        inProgressStatus.setName(OrderStatusEnum.IN_PROGRESS.toString());
        orderStatusRepository.save(inProgressStatus);

        // Создаем заказ
        Order order = new Order();
        order.setClient(clientProfile);
        order.setMaster(masterProfile);
        order.setStatus(pendingStatus);
        order.setPrice(100L);
        orderRepository.save(order);

        // Создаем баланс клиента
        FinanceBalance clientFinanceBalance = new FinanceBalance();
        clientFinanceBalance.setUser(clientUser);
        clientFinanceBalance.setBalance(500L); // Достаточный баланс
        financeBalanceRepository.save(clientFinanceBalance);

        // Act
        orderService.acceptOrder(order.getId(), "testmaster");

        // Assert
        // Проверяем, что статус заказа изменился на "IN_PROGRESS"
        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatusEnum.IN_PROGRESS.toString(), updatedOrder.getStatus().getName());

        // Проверяем, что баланс клиента обновлен
        FinanceBalance updatedClientFinanceBalance = financeBalanceRepository.findByUser_Id(clientUser.getId()).orElseThrow();
        assertEquals(400L, updatedClientFinanceBalance.getBalance()); // 500 - 100 = 400
    }

    @Test
    void testDeclineOrder_Success() {
        // Arrange
        // Создаем пользователя-мастера
        User masterUser = new User();
        masterUser.setLogin("testmaster");
        userRepository.save(masterUser);

        MasterProfile masterProfile = new MasterProfile();
        masterProfile.setUser(masterUser);
        masterProfileRepository.save(masterProfile);

        // Создаем пользователя-клиента
        User clientUser = new User();
        clientUser.setLogin("testclient");
        userRepository.save(clientUser);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(clientUser);
        clientProfileRepository.save(clientProfile);

        // Создаем статусы
        OrderStatus pendingStatus = new OrderStatus();
        pendingStatus.setName(OrderStatusEnum.PENDING.toString());
        orderStatusRepository.save(pendingStatus);

        OrderStatus declinedStatus = new OrderStatus();
        declinedStatus.setName(OrderStatusEnum.DECLINED.toString());
        orderStatusRepository.save(declinedStatus);

        // Создаем заказ
        Order order = new Order();
        order.setClient(clientProfile);
        order.setMaster(masterProfile);
        order.setStatus(pendingStatus);
        orderRepository.save(order);

        // Act
        orderService.declineOrder(order.getId(), "testmaster");

        // Assert
        // Проверяем, что статус заказа изменился на "DECLINED"
        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatusEnum.DECLINED.toString(), updatedOrder.getStatus().getName());
    }

    @Test
    void testAcceptOrder_OrderNotBelongToMaster() {
        // Arrange
        // Создаем двух мастеров
        User masterUser1 = new User();
        masterUser1.setLogin("testmaster1");
        userRepository.save(masterUser1);

        User masterUser2 = new User();
        masterUser2.setLogin("testmaster2");
        userRepository.save(masterUser2);

        MasterProfile masterProfile1 = new MasterProfile();
        masterProfile1.setUser(masterUser1);
        masterProfileRepository.save(masterProfile1);

        MasterProfile masterProfile2 = new MasterProfile();
        masterProfile2.setUser(masterUser2);
        masterProfileRepository.save(masterProfile2);

        // Создаем пользователя-клиента
        User clientUser = new User();
        clientUser.setLogin("testclient");
        userRepository.save(clientUser);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(clientUser);
        clientProfileRepository.save(clientProfile);

        // Создаем статус "PENDING"
        OrderStatus pendingStatus = new OrderStatus();
        pendingStatus.setName(OrderStatusEnum.PENDING.toString());
        orderStatusRepository.save(pendingStatus);

        // Создаем заказ для первого мастера
        Order order = new Order();
        order.setClient(clientProfile);
        order.setMaster(masterProfile1);
        order.setStatus(pendingStatus);
        order.setPrice(100L);
        orderRepository.save(order);

        // Act & Assert
        ConflictResourceException exception = assertThrows(ConflictResourceException.class, () -> {
            orderService.acceptOrder(order.getId(), "testmaster2"); // Второй мастер пытается принять заказ
        });

        // Проверяем сообщение исключения
        assertEquals("OrderNotBelongToMaster", exception.getMessage());
    }

    @Test
    void testAcceptOrder_NotEnoughMoney() {
        // Arrange
        // Создаем пользователя-мастера
        User masterUser = new User();
        masterUser.setLogin("testmaster");
        userRepository.save(masterUser);

        MasterProfile masterProfile = new MasterProfile();
        masterProfile.setUser(masterUser);
        masterProfileRepository.save(masterProfile);

        // Создаем пользователя-клиента
        User clientUser = new User();
        clientUser.setLogin("testclient");
        userRepository.save(clientUser);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(clientUser);
        clientProfileRepository.save(clientProfile);

        // Создаем статус "PENDING"
        OrderStatus pendingStatus = new OrderStatus();
        pendingStatus.setName(OrderStatusEnum.PENDING.toString());
        orderStatusRepository.save(pendingStatus);

        // Создаем заказ
        Order order = new Order();
        order.setClient(clientProfile);
        order.setMaster(masterProfile);
        order.setStatus(pendingStatus);
        order.setPrice(100L);
        orderRepository.save(order);

        // Создаем баланс клиента с недостаточным количеством средств
        FinanceBalance clientFinanceBalance = new FinanceBalance();
        clientFinanceBalance.setUser(clientUser);
        clientFinanceBalance.setBalance(50L); // Недостаточный баланс
        financeBalanceRepository.save(clientFinanceBalance);

        // Act & Assert
        ConflictResourceException exception = assertThrows(ConflictResourceException.class, () -> {
            orderService.acceptOrder(order.getId(), "testmaster");
        });

        // Проверяем сообщение исключения
        assertEquals("NotEnoughMoney", exception.getMessage());

        // Проверяем, что заказ удален
        assertFalse(orderRepository.findById(order.getId()).isPresent());
    }

    @Test
    void testDeclineOrder_OrderNotBelongToMaster() {
        // Arrange
        // Создаем двух мастеров
        User masterUser1 = new User();
        masterUser1.setLogin("testmaster1");
        userRepository.save(masterUser1);

        User masterUser2 = new User();
        masterUser2.setLogin("testmaster2");
        userRepository.save(masterUser2);

        MasterProfile masterProfile1 = new MasterProfile();
        masterProfile1.setUser(masterUser1);
        masterProfileRepository.save(masterProfile1);

        MasterProfile masterProfile2 = new MasterProfile();
        masterProfile2.setUser(masterUser2);
        masterProfileRepository.save(masterProfile2);

        // Создаем пользователя-клиента
        User clientUser = new User();
        clientUser.setLogin("testclient");
        userRepository.save(clientUser);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(clientUser);
        clientProfileRepository.save(clientProfile);

        // Создаем статус "PENDING"
        OrderStatus pendingStatus = new OrderStatus();
        pendingStatus.setName(OrderStatusEnum.PENDING.toString());
        orderStatusRepository.save(pendingStatus);

        // Создаем заказ для первого мастера
        Order order = new Order();
        order.setClient(clientProfile);
        order.setMaster(masterProfile1);
        order.setStatus(pendingStatus);
        orderRepository.save(order);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.declineOrder(order.getId(), "testmaster2"); // Второй мастер пытается отказаться от заказа
        });

        // Проверяем сообщение исключения
        assertEquals("OrderNotBelongToMaster", exception.getMessage());
    }

    @Test
    void testCancelOrder_Success() {
        // Arrange
        User clientUser = new User();
        clientUser.setLogin("testclient");
        userRepository.save(clientUser);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(clientUser);
        clientProfileRepository.save(clientProfile);

        OrderStatus placedStatus = new OrderStatus();
        placedStatus.setName(OrderStatusEnum.PLACED.toString());
        orderStatusRepository.save(placedStatus);

        Order order = new Order();
        order.setClient(clientProfile);
        order.setStatus(placedStatus);
        order.setPrice(100L);
        orderRepository.save(order);

        FinanceBalance financeBalance = new FinanceBalance();
        financeBalance.setUser(clientUser);
        financeBalance.setBalance(500L);
        financeBalanceRepository.save(financeBalance);

        // Act
        orderService.cancelOrder(order.getId(), "testclient");

        // Assert
        FinanceBalance updatedFinanceBalance = financeBalanceRepository.findByUser_Id(clientUser.getId()).orElseThrow();
        assertEquals(600L, updatedFinanceBalance.getBalance()); // 500 + 100 = 600

        assertFalse(orderRepository.findById(order.getId()).isPresent()); // Заказ удален
    }

    @Test
    void testCancelOrder_OrderAlreadyBelongToMaster() {
        // Arrange
        User clientUser = new User();
        clientUser.setLogin("testclient");
        userRepository.save(clientUser);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(clientUser);
        clientProfileRepository.save(clientProfile);

        User masterUser = new User();
        masterUser.setLogin("testmaster");
        userRepository.save(masterUser);

        MasterProfile masterProfile = new MasterProfile();
        masterProfile.setUser(masterUser);
        masterProfileRepository.save(masterProfile);

        OrderStatus placedStatus = new OrderStatus();
        placedStatus.setName(OrderStatusEnum.PLACED.toString());
        orderStatusRepository.save(placedStatus);

        Order order = new Order();
        order.setClient(clientProfile);
        order.setMaster(masterProfile); // Заказ уже назначен мастеру
        order.setStatus(placedStatus);
        order.setPrice(100L);
        orderRepository.save(order);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.cancelOrder(order.getId(), "testclient");
        });

        assertEquals("OrderAlreadyBelongToMaster", exception.getMessage());
    }

    @Test
    void testCancelOrder_OrderNotBelongToClient() {
        // Arrange
        User clientUser1 = new User();
        clientUser1.setLogin("testclient1");
        userRepository.save(clientUser1);

        User clientUser2 = new User();
        clientUser2.setLogin("testclient2");
        userRepository.save(clientUser2);

        ClientProfile clientProfile1 = new ClientProfile();
        clientProfile1.setUser(clientUser1);
        clientProfileRepository.save(clientProfile1);

        ClientProfile clientProfile2 = new ClientProfile();
        clientProfile2.setUser(clientUser2);
        clientProfileRepository.save(clientProfile2);

        OrderStatus placedStatus = new OrderStatus();
        placedStatus.setName(OrderStatusEnum.PLACED.toString());
        orderStatusRepository.save(placedStatus);

        Order order = new Order();
        order.setClient(clientProfile1);
        order.setStatus(placedStatus);
        order.setPrice(100L);
        orderRepository.save(order);

        // Act & Assert
        ConflictResourceException exception = assertThrows(ConflictResourceException.class, () -> {
            orderService.cancelOrder(order.getId(), "testclient2"); // Второй клиент пытается отменить заказ
        });

        assertEquals("OrderNotBelongToClient", exception.getMessage());
    }

    @Test
    void testGetClientOrders_Success() {
        // Arrange
        User clientUser = new User();
        clientUser.setLogin("testclient");
        userRepository.save(clientUser);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(clientUser);
        clientProfileRepository.save(clientProfile);

        OrderStatus inProgressStatus = new OrderStatus();
        inProgressStatus.setName(OrderStatusEnum.IN_PROGRESS.toString());
        orderStatusRepository.save(inProgressStatus);

        OrderStatus placedStatus = new OrderStatus();
        placedStatus.setName(OrderStatusEnum.PLACED.toString());
        orderStatusRepository.save(placedStatus);

        Order order1 = new Order();
        order1.setClient(clientProfile);
        order1.setStatus(inProgressStatus);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setClient(clientProfile);
        order2.setStatus(placedStatus);
        orderRepository.save(order2);

        // Act
        List<Order> orders = orderService.getClientOrders("testclient");

        // Assert
        assertEquals(2, orders.size());
        assertEquals(OrderStatusEnum.IN_PROGRESS.toString(), orders.get(0).getStatus().getName());
        assertEquals(OrderStatusEnum.PLACED.toString(), orders.get(1).getStatus().getName());
    }

    @Test
    void testGetClientOrders_NoOrders() {
        // Arrange
        User clientUser = new User();
        clientUser.setLogin("testclient");
        userRepository.save(clientUser);

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setUser(clientUser);
        clientProfileRepository.save(clientProfile);

        // Act
        List<Order> orders = orderService.getClientOrders("testclient");

        // Assert
        assertTrue(orders.isEmpty());
    }

    @Test
    void testGetMasterOrders_Success() {
        // Arrange
        User masterUser = new User();
        masterUser.setLogin("testmaster");
        userRepository.save(masterUser);

        MasterProfile masterProfile = new MasterProfile();
        masterProfile.setUser(masterUser);
        masterProfileRepository.save(masterProfile);

        OrderStatus inProgressStatus = new OrderStatus();
        inProgressStatus.setName(OrderStatusEnum.IN_PROGRESS.toString());
        orderStatusRepository.save(inProgressStatus);

        OrderStatus placedStatus = new OrderStatus();
        placedStatus.setName(OrderStatusEnum.PLACED.toString());
        orderStatusRepository.save(placedStatus);

        Order order1 = new Order();
        order1.setMaster(masterProfile);
        order1.setStatus(inProgressStatus);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setMaster(masterProfile);
        order2.setStatus(placedStatus);
        orderRepository.save(order2);

        // Act
        List<Order> orders = orderService.getMasterOrders("testmaster");

        // Assert
        assertEquals(2, orders.size());
        assertEquals(OrderStatusEnum.IN_PROGRESS.toString(), orders.get(0).getStatus().getName());
        assertEquals(OrderStatusEnum.PLACED.toString(), orders.get(1).getStatus().getName());
    }

    @Test
    void testGetMasterOrders_NoOrders() {
        // Arrange
        User masterUser = new User();
        masterUser.setLogin("testmaster");
        userRepository.save(masterUser);

        MasterProfile masterProfile = new MasterProfile();
        masterProfile.setUser(masterUser);
        masterProfileRepository.save(masterProfile);

        // Act
        List<Order> orders = orderService.getMasterOrders("testmaster");

        // Assert
        assertTrue(orders.isEmpty());
    }

}