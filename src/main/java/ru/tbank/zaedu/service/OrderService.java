package ru.tbank.zaedu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.models.Order;
import ru.tbank.zaedu.models.User;
import ru.tbank.zaedu.repo.OrderRepository;
import ru.tbank.zaedu.repo.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    public List<Order> getClientOrders(String name) {
        Optional<User> user = userRepository.findByLogin(name);
        List<Order> orders = orderRepository.findByClientId(user.get().getId());

        Map<String, Integer> statusOrder = Map.of(
                "IN_PROGRESS", 1,
                "PENDING", 2,
                "COMPLETED", 3
        );

        orders.sort(Comparator.comparingInt(
                order -> statusOrder.getOrDefault(order.getStatus().getName(), Integer.MAX_VALUE)
        ));

        return orders;
    }
}
