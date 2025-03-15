package ru.tbank.zaedu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.repo.ClientProfileRepository;
import ru.tbank.zaedu.models.ClientProfile;
import ru.tbank.zaedu.repo.OrderRepository;
import ru.tbank.zaedu.models.Order;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientProfileRepository clientProfileRepository;
    private final OrderRepository orderRepository;

    public Optional<ClientProfile> getClientProfileById(long clientId) {
        return clientProfileRepository.findById(clientId);
    }

    public List<Order> getClientOrders(long clientId) {
        return orderRepository.findByClientId(clientId);
    }
}
