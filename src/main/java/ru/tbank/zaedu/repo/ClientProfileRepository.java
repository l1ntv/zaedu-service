package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tbank.zaedu.models.ClientProfile;

@Repository
public interface ClientProfileRepository extends JpaRepository<ClientProfile, Long> {

}
