package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.zaedu.models.MasterServiceEntity;

public interface MasterServiceEntityRepository extends JpaRepository<MasterServiceEntity, Long> {
}
