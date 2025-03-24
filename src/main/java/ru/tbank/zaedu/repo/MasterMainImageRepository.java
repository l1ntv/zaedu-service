package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.zaedu.models.MasterMainImage;

import java.util.Optional;

public interface MasterMainImageRepository extends JpaRepository<MasterMainImage, Long> {
    Optional<MasterMainImage> findByMasterId(Long id);
}
