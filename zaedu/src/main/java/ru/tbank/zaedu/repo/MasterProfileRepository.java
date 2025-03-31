package ru.tbank.zaedu.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tbank.zaedu.models.MasterProfile;

@Repository
public interface MasterProfileRepository extends JpaRepository<MasterProfile, Long> {
    @Query("SELECT DISTINCT m FROM MasterProfile m " + "JOIN FETCH m.services ms "
            + "JOIN FETCH ms.services s "
            + "WHERE s.name = :category")
    List<MasterProfile> findByServiceCategory(@Param("category") String category);

    Optional<MasterProfile> findByUser_Id(Long id);

    Optional<MasterProfile> findByUser_Login(String login);

    Optional<MasterProfile> findByPassportSeriesAndPassportNumber(String series, String number);
}
