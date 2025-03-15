package ru.tbank.zaedu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.tbank.zaedu.models.ClientProfile;
import ru.tbank.zaedu.models.Order;
import ru.tbank.zaedu.service.ClientService;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{clientId}/main")
    public ResponseEntity<?> getClientMainInfo(@PathVariable long clientId) {
        ClientProfile clientProfile = clientService.getClientProfileById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(clientProfile);
    }

    @GetMapping("/{clientId}/orders")
    public ResponseEntity<?> getClientOrders(@PathVariable long clientId) {
        List<Order> orders = clientService.getClientOrders(clientId);
        return ResponseEntity.ok(orders);
    }
}
