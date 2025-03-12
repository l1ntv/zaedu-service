package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tbank.zaedu.models.MasterProfile;

import java.util.List;

public interface MasterProfileRepository extends JpaRepository<MasterProfile, Long> {
    List<MasterProfile> findByServices_Name(String serviceName);
}
