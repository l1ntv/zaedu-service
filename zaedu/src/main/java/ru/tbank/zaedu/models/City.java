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
@Table(name = "cities")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "cities_seq")
public class City extends AbstractEntity {
    private String name;

    @OneToMany(mappedBy = "city")
    @ToString.Exclude
    private List<ClientProfile> clients;

    @OneToMany(mappedBy = "city")
    @ToString.Exclude
    private List<MasterProfile> masters;

    @OneToMany(mappedBy = "city")
    @ToString.Exclude
    private List<Hood> hoods;
}
