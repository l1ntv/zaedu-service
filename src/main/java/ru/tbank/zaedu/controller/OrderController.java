package ru.tbank.zaedu.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.zaedu.DTO.OrderDTO;
import ru.tbank.zaedu.models.Order;
import ru.tbank.zaedu.service.OrderService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/clients")
public class OrderController extends EntityController<Order> {

    private static final Class<OrderDTO> DTO_CLASS = OrderDTO.class;

    private final OrderService orderService;

    public OrderController(ModelMapper modelMapper, OrderService orderService) {
        super(modelMapper);
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getClientOrders(Principal principal) {
        List<Order> orders = orderService.getClientOrders(principal.getName());
        return ResponseEntity.ok(serialize(orders, DTO_CLASS));
    }
}
