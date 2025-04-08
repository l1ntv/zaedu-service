package ru.tbank.zaedu.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.zaedu.models.*;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClient_Id(Long clientId);

    List<Order> findByClient(ClientProfile client);

    List<Order> findByMaster(MasterProfile masterProfile);

    List<Order> findByMaster_Id(Long masterId);

    List<Order> findByStatusAndServicesIn(OrderStatus orderStatus, List<Services> services);

    Optional<Order> findByIdAndClient_Id(Long id, Long clientProfileId);

    List<Order> findByClientAndServicesAndAddressAndDateFromLessThanEqualAndDateToGreaterThanEqual(
            ClientProfile clientProfile, Services services, String address, LocalDate dateFrom, LocalDate dateTo);
}
