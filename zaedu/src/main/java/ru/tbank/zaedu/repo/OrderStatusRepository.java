package src.main.java.ru.tbank.zaedu.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import src.main.java.ru.tbank.zaedu.models.OrderStatus;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    Optional<OrderStatus> findByName(String name);


}
