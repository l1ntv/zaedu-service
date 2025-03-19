package ru.tbank.zaedu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.tbank.zaedu.DTO.ClientProfileDTO;
import ru.tbank.zaedu.DTO.OrderDTO;
import ru.tbank.zaedu.service.ClientService;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    // вместо clientId делаем Principal /my-profile
    // /profile-for-other
    @GetMapping("/{clientId}/main")
    public ResponseEntity<?> getClientMainInfo(@PathVariable long clientId) {
        ClientProfileDTO clientProfileDTO = clientService.getClientProfileById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(clientProfileDTO);
    }

    // вместо clientId делаем Principal /my-profile
    // отсортировать заказы
    @GetMapping("/{clientId}/orders")
    public ResponseEntity<List<OrderDTO>> getClientOrders(@PathVariable long clientId) {
        List<OrderDTO> orders = clientService.getClientOrders(clientId);
        return ResponseEntity.ok(orders);
    }

    // сделать ручку для обновления профиля, использовать Principal
}
