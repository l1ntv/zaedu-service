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
@Table(name = "finance_balance")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "finance_balance_seq")
public class FinanceBalance extends AbstractEntity {
    private Long balance;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
