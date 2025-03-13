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
@Table(name = "master_feedback")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "master_feedback_seq")
public class MasterFeedback extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "master_id")
    private MasterProfile master;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientProfile client;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer evaluation;
    private String description;
}
