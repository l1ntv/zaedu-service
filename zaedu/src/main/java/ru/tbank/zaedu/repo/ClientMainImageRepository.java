package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.zaedu.models.ClientMainImage;

import java.util.Optional;
import java.util.UUID;

public interface ClientMainImageRepository extends JpaRepository<ClientMainImage, Long> {
    Optional<ClientMainImage> findByUploadId(UUID uploadId);
}
