package ru.tbank.zaedu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import ru.tbank.zaedu.service.OrderService;

import java.security.Principal;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Отображение исполнителю выставленных клиентами заказов
    @GetMapping
    public ResponseEntity<ClientsOrdersResponse> findPlacedOrdersByClients(Principal principal) {
        String masterLogin = principal.getName();
        return ResponseEntity.ok(orderService.findPlacedOrdersByClients(masterLogin));
    }

    // Нажатие исполнителем кнопки "Взять заказ"; взятие заказа выставленного клиентом
    @PatchMapping("/{id}/get-order")
    public ResponseEntity<Void> getPlacedOrder(@PathVariable Long id, Principal principal) {
        String masterLogin = principal.getName();

        return ResponseEntity.ok().build();
    }

}
