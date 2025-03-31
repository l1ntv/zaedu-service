package src.main.java.ru.tbank.zaedu.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import src.main.java.ru.tbank.zaedu.models.ClientProfile;
import src.main.java.ru.tbank.zaedu.models.Order;
import src.main.java.ru.tbank.zaedu.models.OrderStatus;
import src.main.java.ru.tbank.zaedu.models.Services;

import java.time.LocalDate;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClient_Id(Long clientId);

    List<Order> findByMaster_Id(Long masterId);

    List<Order> findByStatus(OrderStatus orderStatus);

    Optional<Order> findByIdAndClient_Id(Long id, Long clientProfileId);

    List<Order> findByClientAndServicesAndAddressAndDateFromLessThanEqualAndDateToGreaterThanEqual(
            ClientProfile clientProfile,
            Services services,
            String address,
            LocalDate dateFrom,
            LocalDate dateTo
    );
}
