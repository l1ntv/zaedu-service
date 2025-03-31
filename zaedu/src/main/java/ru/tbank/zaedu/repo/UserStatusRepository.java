package src.main.java.ru.tbank.zaedu.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import src.main.java.ru.tbank.zaedu.models.UserStatus;

public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {
    Optional<UserStatus> findByName(String name);
}
