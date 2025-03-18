package ru.tbank.zaedu.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import static ru.tbank.zaedu.models.AbstractEntity.DEFAULT_GENERATOR;

@Accessors(chain = true)
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "master_hoods")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "master_hoods_seq")
public class MasterHoodsEntity extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "master_id", nullable = false)
    private MasterProfile master;

    @ManyToOne
    @JoinColumn(name = "hood_id", nullable = false)
    private Hood hood;
}
