package ru.tbank.zaedu.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.zaedu.models.City;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByName(String name);
}
