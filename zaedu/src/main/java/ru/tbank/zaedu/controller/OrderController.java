package ru.tbank.zaedu.controller;

import java.security.Principal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.zaedu.DTO.*;
import ru.tbank.zaedu.enums.ServicesEnum;
import ru.tbank.zaedu.models.Order;
import ru.tbank.zaedu.service.OrderService;

@RestController
@RequestMapping
public class OrderController extends EntityController<Order> {

    private static final Class<OrderClientDTO> ORDER_CLIENT_DTO_CLASS = OrderClientDTO.class;
    private static final Class<OrderMasterDTO> ORDER_MASTER_DTO_CLASS = OrderMasterDTO.class;

    private final OrderService orderService;

    public OrderController(ModelMapper modelMapper, OrderService orderService) {
        super(modelMapper);
        this.orderService = orderService;
    }

    // Отображение заказов клиента
    // + возвращать статус заказа и id
    @GetMapping("/clients/my-orders")
    public ResponseEntity<List<OrderClientDTO>> getClientOrders(Principal principal) {
        List<Order> orders = orderService.getClientOrders(principal.getName());
        return ResponseEntity.ok(serialize(orders, ORDER_CLIENT_DTO_CLASS));
    }

    // + возвращать статус заказа и id
    @GetMapping("/masters/my-orders")
    public ResponseEntity<List<OrderMasterDTO>> getMasterOrders(Principal principal) {
        List<Order> orders = orderService.getMasterOrders(principal.getName());
        return ResponseEntity.ok(serialize(orders, ORDER_MASTER_DTO_CLASS));
    }

    // Отображение исполнителю выставленных клиентами заказов
    @GetMapping("/orders")
    public ResponseEntity<ClientsOrdersResponse> findPlacedOrdersByClients(Principal principal) {
        String masterLogin = principal.getName();
        return ResponseEntity.ok(orderService.findPlacedOrdersByClients(masterLogin));
    }

    // Нажатие исполнителем кнопки "Взять заказ"; взятие заказа выставленного клиентом; id заказа
    @PatchMapping("/orders/{orderId}/assign-order")
    public ResponseEntity<Void> assignPlacedOrder(@PathVariable Long orderId, Principal principal) {
        // Нажатие исполнителем кнопки "Взять заказ"; взятие заказа выставленного клиентом
        String masterLogin = principal.getName();
        orderService.assignOrderToMaster(orderId, masterLogin);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders/get-services")
    public ResponseEntity<EnumServicesResponse> getEnumServices() {
        return ResponseEntity.ok(new EnumServicesResponse(List.of(
                ServicesEnum.DECORATOR.toString(),
                ServicesEnum.SOUND_ENGINEER.toString(),
                ServicesEnum.LIGHT_ENGINEER.toString(),
                ServicesEnum.PHOTOGRAPHER.toString(),
                ServicesEnum.VIDEOGRAPHER.toString(),
                ServicesEnum.CHEF.toString(),
                ServicesEnum.WAITER.toString(),
                ServicesEnum.HOST.toString())));
    }

    @PostMapping("/orders/create-order")
    public ResponseEntity<Void> createOrder(@RequestBody CreatedOrderRequest request, Principal principal) {
        String clientLogin = principal.getName();
        orderService.createOrder(request, clientLogin);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/orders/{orderId}/close-order")
    public ResponseEntity<Void> closeOrder(@PathVariable Long orderId, Principal principal) {
        String clientLogin = principal.getName();
        orderService.closeOrder(orderId, clientLogin);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{masterId}/offer-order")
    public ResponseEntity<Void> offerOrder(
            @PathVariable Long masterId, @RequestBody CreatedOrderRequest request, Principal principal) {
        String clientLogin = principal.getName();
        orderService.offerOrder(masterId, request, clientLogin);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/orders/{orderId}/accept-order")
    public ResponseEntity<Void> acceptOrder(@PathVariable Long orderId, Principal principal) {
        String masterLogin = principal.getName();
        orderService.acceptOrder(orderId, masterLogin);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/orders/{orderId}/decline-order")
    public ResponseEntity<Void> declineOrder(@PathVariable Long orderId, Principal principal) {
        String masterLogin = principal.getName();
        orderService.declineOrder(orderId, masterLogin);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/orders/{orderId}/cancel-order")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId, Principal principal) {
        String clientLogin = principal.getName();
        orderService.cancelOrder(orderId, clientLogin);
        return ResponseEntity.ok().build();
    }
}
