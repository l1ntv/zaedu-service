package src.main.java.ru.tbank.zaedu.models;

import static src.main.java.ru.tbank.zaedu.models.AbstractEntity.DEFAULT_GENERATOR;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_roles")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "user_roles_seq")
public class UserRole extends AbstractEntity {
    private String name;

    @OneToMany(mappedBy = "role")
    @ToString.Exclude
    private List<User> users;
}
