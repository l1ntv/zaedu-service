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
