package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.zaedu.models.Order;
import ru.tbank.zaedu.models.OrderStatus;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientId(long clientId);

    List<Order> findByStatus(OrderStatus orderStatus);
}
