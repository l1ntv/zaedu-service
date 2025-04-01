package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.zaedu.models.MasterFeedback;
import ru.tbank.zaedu.models.Order;

import java.util.List;

@Repository
public interface MasterFeedbackRepository extends JpaRepository<MasterFeedback, Long> {

    List<MasterFeedback> findByOrder(Order order);

}
