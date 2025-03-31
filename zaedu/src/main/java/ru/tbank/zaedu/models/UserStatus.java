package ru.tbank.zaedu.models;

import static ru.tbank.zaedu.models.AbstractEntity.DEFAULT_GENERATOR;

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
@Table(name = "user_statuses")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "user_statuses_seq")
public class UserStatus extends AbstractEntity {
    private String name;

    @OneToMany(mappedBy = "status")
    @ToString.Exclude
    private List<User> users;
}
