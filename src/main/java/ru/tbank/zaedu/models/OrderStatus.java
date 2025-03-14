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
@Table(name = "order_statuses")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "order_statuses_seq")
public class OrderStatus extends AbstractEntity {
    private String name;

    @OneToMany(mappedBy = "status")
    @ToString.Exclude
    private List<Order> orders;
}
