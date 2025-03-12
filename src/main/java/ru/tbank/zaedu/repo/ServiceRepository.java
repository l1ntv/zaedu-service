package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.zaedu.models.Services;

import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Services, Long> {
    Optional<Services> findById(Long id); // Для поиска услуги по ID
}