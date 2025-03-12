package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.zaedu.models.Hood;

import java.util.Optional;

@Repository
public interface HoodRepository extends JpaRepository<Hood, Long> {
    Optional<Hood> findByName(String name); // Для поиска района по названию
}
