package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.zaedu.models.MasterProfile;
import ru.tbank.zaedu.models.MasterServiceEntity;

import java.util.List;

public interface MasterServiceEntityRepository extends JpaRepository<MasterServiceEntity, Long> {
    List<MasterServiceEntity> findByMaster(MasterProfile masterProfile);
}
