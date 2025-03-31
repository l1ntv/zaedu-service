package src.main.java.ru.tbank.zaedu.models;

import static src.main.java.ru.tbank.zaedu.models.AbstractEntity.DEFAULT_GENERATOR;

import jakarta.persistence.*;
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
@Table(name = "master_services")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "master_services_seq")
public class MasterServiceEntity extends AbstractEntity {
    private Long price;

    @ManyToOne
    @JoinColumn(name = "master_id")
    private MasterProfile master;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Services services;
}
