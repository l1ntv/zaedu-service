package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.zaedu.models.City;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByName(String name);
}
