package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.zaedu.models.ClientProfile;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, Long> {

}
