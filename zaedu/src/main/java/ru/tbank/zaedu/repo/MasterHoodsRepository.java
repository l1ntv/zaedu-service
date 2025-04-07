package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.zaedu.models.MasterHoodsEntity;


@Repository
public interface MasterHoodsRepository extends JpaRepository<MasterHoodsEntity, Long> {

}
