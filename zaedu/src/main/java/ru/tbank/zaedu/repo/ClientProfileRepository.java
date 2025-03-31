package src.main.java.ru.tbank.zaedu.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import src.main.java.ru.tbank.zaedu.models.ClientProfile;
import src.main.java.ru.tbank.zaedu.models.User;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, Long> {
    Optional<ClientProfile> findByUser_Id(Long id);

    Optional<ClientProfile> findByUser(User user);
}
