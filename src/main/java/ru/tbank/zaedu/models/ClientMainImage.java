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
@Table(name = "client_main_images")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "client_main_images_seq")
public class ClientMainImage extends AbstractEntity {
    @OneToOne
    @JoinColumn(name = "client_id")
    private ClientProfile client;

    private String url;
}
