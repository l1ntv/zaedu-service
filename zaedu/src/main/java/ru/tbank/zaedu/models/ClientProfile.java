package ru.tbank.zaedu.models;

import static ru.tbank.zaedu.models.AbstractEntity.DEFAULT_GENERATOR;

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
@Table(name = "client_profile")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "client_profile_seq")
public class ClientProfile extends AbstractEntity {
    private String surname;
    private String name;
    private String patronymic;
    private String email;
    private String telephoneNumber;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @OneToOne
    @JoinColumn(name = "client_id")
    private User user;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private ClientMainImage mainImage;
}
