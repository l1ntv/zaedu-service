package ru.tbank.zaedu.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.zaedu.models.Services;

@Repository
public interface ServiceRepository extends JpaRepository<Services, Long> {
    Optional<Services> findById(Long id); // Для поиска услуги по ID

    Optional<Services> findByName(String name);
}
