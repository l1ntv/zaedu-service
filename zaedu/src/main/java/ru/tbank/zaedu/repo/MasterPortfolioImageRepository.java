package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.zaedu.models.MasterPortfolioImage;

import java.util.Optional;
import java.util.UUID;

public interface MasterPortfolioImageRepository extends JpaRepository<MasterPortfolioImage, Long>  {
    Optional<MasterPortfolioImage> findByFilename(String filename);

    Optional<MasterPortfolioImage> findByUploadId(UUID uploadId);
}
