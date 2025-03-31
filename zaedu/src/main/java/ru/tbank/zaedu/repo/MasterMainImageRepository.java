package src.main.java.ru.tbank.zaedu.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import src.main.java.ru.tbank.zaedu.models.MasterMainImage;

public interface MasterMainImageRepository extends JpaRepository<MasterMainImage, Long> {
    Optional<MasterMainImage> findByMasterId(Long id);
}
