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
@Table(name = "services")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "services_seq")
public class Services extends AbstractEntity {
    private String name;

    @OneToMany(mappedBy = "services")
    @ToString.Exclude
    private List<MasterServiceEntity> masterServices;

    @OneToMany(mappedBy = "services")
    @ToString.Exclude
    private List<Order> orders;
}
