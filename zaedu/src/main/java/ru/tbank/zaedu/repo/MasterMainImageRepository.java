package ru.tbank.zaedu.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.zaedu.models.MasterMainImage;

public interface MasterMainImageRepository extends JpaRepository<MasterMainImage, Long> {
    Optional<MasterMainImage> findByMasterId(Long id);
    Optional<MasterMainImage> findByUploadId(UUID uploadId);
}
