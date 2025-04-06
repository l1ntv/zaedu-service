package ru.tbank.zaedu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.zaedu.models.Order;
import ru.tbank.zaedu.repo.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderDeletionService {

    private final OrderRepository orderRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteOrderInNewTransaction(Order order) {
        orderRepository.delete(order);
        orderRepository.flush();
    }
}