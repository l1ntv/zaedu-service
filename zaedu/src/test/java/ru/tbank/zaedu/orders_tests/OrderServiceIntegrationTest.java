package ru.tbank.zaedu.orders_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tbank.zaedu.AbstractIntegrationTest;
import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import ru.tbank.zaedu.DTO.CreatedOrderRequest;
import ru.tbank.zaedu.enums.OrderStatusEnum;
import ru.tbank.zaedu.models.*;
import ru.tbank.zaedu.repo.*;
import ru.tbank.zaedu.service.OrderServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        userRepository.deleteAll();
        masterProfileRepository.deleteAll();
        serviceRepository.deleteAll();
        orderRepository.deleteAll();
        financeBalanceRepository.deleteAll();
        masterMainImageRepository.deleteAll();
        clientProfileRepository.deleteAll();
        orderStatusRepository.deleteAll();
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
}