package ru.tbank.zaedu.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

import static ru.tbank.zaedu.models.AbstractEntity.DEFAULT_GENERATOR;

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
    private List<User> users;
}