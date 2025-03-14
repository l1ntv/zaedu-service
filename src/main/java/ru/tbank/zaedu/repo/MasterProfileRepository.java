package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tbank.zaedu.models.MasterProfile;
import ru.tbank.zaedu.models.Services;

import java.util.List;

@Repository
public interface MasterProfileRepository extends JpaRepository<MasterProfile, Long> {
//    List<MasterProfile> findByServices_Name(String serviceName);
}
