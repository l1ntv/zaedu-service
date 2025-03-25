package ru.tbank.zaedu.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import ru.tbank.zaedu.DTO.OrderDTO;
import ru.tbank.zaedu.models.Order;
import ru.tbank.zaedu.service.OrderService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping
public class OrderController extends EntityController<Order> {

    private static final Class<OrderDTO> DTO_CLASS = OrderDTO.class;

    private final OrderService orderService;

    public OrderController(ModelMapper modelMapper, OrderService orderService) {
        super(modelMapper);
        this.orderService = orderService;
    }

    @GetMapping("/clients/orders")
    public ResponseEntity<List<OrderDTO>> getClientOrders(Principal principal) {
        List<Order> orders = orderService.getClientOrders(principal.getName());
        return ResponseEntity.ok(serialize(orders, DTO_CLASS));
    }

    // Отображение исполнителю выставленных клиентами заказов
    @GetMapping("/orders")
    public ResponseEntity<ClientsOrdersResponse> findPlacedOrdersByClients(Principal principal) {
        String masterLogin = principal.getName();
        return ResponseEntity.ok(orderService.findPlacedOrdersByClients(masterLogin));
    }

    // Нажатие исполнителем кнопки "Взять заказ"; взятие заказа выставленного клиентом
    @PatchMapping("/orders/{id}/get-order")
    public ResponseEntity<Void> getPlacedOrder(@PathVariable Long id, Principal principal) {
        String masterLogin = principal.getName();



        return ResponseEntity.ok().build();
    }
}
