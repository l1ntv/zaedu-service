package ru.tbank.zaedu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.zaedu.models.ClientProfile;
import ru.tbank.zaedu.models.User;

import java.util.Optional;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, Long> {
    Optional<ClientProfile> findByUser(User user);
}
