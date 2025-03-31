package src.main.java.ru.tbank.zaedu.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.main.java.ru.tbank.zaedu.models.Hood;

@Repository
public interface HoodRepository extends JpaRepository<Hood, Long> {
    Optional<Hood> findByName(String name); // Используем поле name из сущности Hood
}
