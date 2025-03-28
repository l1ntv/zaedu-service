package ru.tbank.zaedu.models;

import static ru.tbank.zaedu.models.AbstractEntity.DEFAULT_GENERATOR;

import jakarta.persistence.*;
import java.util.ArrayList;
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
@Table(name = "hood")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "hood_seq")
public class Hood extends AbstractEntity {
    private String name;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @OneToMany(mappedBy = "hood", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<MasterHoodsEntity> masters = new ArrayList<>();
}
