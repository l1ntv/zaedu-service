package ru.tbank.zaedu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.zaedu.DTO.ClientsOrdersResponse;
import ru.tbank.zaedu.DTO.CreatedOrderRequest;
import ru.tbank.zaedu.DTO.EnumServicesResponse;
import ru.tbank.zaedu.enums.ServicesEnum;
import ru.tbank.zaedu.service.OrderService;

import java.security.Principal;
import java.util.List;

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

    // Нажатие исполнителем кнопки "Взять заказ"; взятие заказа выставленного клиентом; id заказа
    @PatchMapping("/{orderId}/assign-order")
    public ResponseEntity<Void> assignPlacedOrder(@PathVariable Long orderId, Principal principal) {
        String masterLogin = principal.getName();
        orderService.assignOrderToMaster(orderId, masterLogin);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-services")
    public ResponseEntity<EnumServicesResponse> getEnumServices() {
        return ResponseEntity
                .ok(new EnumServicesResponse(
                        List.of(
                                ServicesEnum.DECORATOR.toString(),
                                ServicesEnum.SOUND_ENGINEER.toString(),
                                ServicesEnum.LIGHT_ENGINEER.toString(),
                                ServicesEnum.PHOTOGRAPHER.toString(),
                                ServicesEnum.VIDEOGRAPHER.toString(),
                                ServicesEnum.CHEF.toString(),
                                ServicesEnum.WAITER.toString(),
                                ServicesEnum.HOST.toString()
                        )));
    }

    @PostMapping("/create-order")
    public ResponseEntity<Void> createOrder(@RequestBody CreatedOrderRequest request, Principal principal) {
        String clientLogin = principal.getName();
        orderService.createOrder(request, clientLogin);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{orderId}/close-order")
    public ResponseEntity<Void> closeOrder(@PathVariable Long orderId, Principal principal) {
        String clientLogin = principal.getName();
        orderService.closeOrder(orderId, clientLogin);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{masterId}/offer-order")
    public ResponseEntity<Void> offerOrder(@PathVariable Long masterId, @RequestBody CreatedOrderRequest request,
                                           Principal principal) {
        String clientLogin = principal.getName();
        orderService.offerOrder(masterId, request, clientLogin);
        return ResponseEntity.ok().build();
    }
}