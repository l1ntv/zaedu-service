package src.main.java.ru.tbank.zaedu.models;

import static src.main.java.ru.tbank.zaedu.models.AbstractEntity.DEFAULT_GENERATOR;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Accessors(chain = true)
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "users_seq")
public class User extends AbstractEntity implements UserDetails {
    private String login;
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private UserStatus status;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + role.getName());
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
