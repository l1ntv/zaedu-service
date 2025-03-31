package ru.tbank.zaedu.models;

import static ru.tbank.zaedu.models.AbstractEntity.DEFAULT_GENERATOR;

import jakarta.persistence.*;
import java.time.LocalDate;
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
@Table(name = "orders")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "order_seq")
public class Order extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    private ClientProfile client;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Services services;

    @ManyToOne
    @JoinColumn(name = "master_id", referencedColumnName = "master_id")
    private MasterProfile master;

    private String description;
    private String address;
    private Long price;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    @ManyToOne
    @JoinColumn(name = "order_status_id")
    private OrderStatus status;
}
