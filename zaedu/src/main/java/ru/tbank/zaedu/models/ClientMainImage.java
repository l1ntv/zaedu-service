package ru.tbank.zaedu.models;

import static ru.tbank.zaedu.models.AbstractEntity.DEFAULT_GENERATOR;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;

import java.util.UUID;

@Accessors(chain = true)
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "client_main_images")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "client_main_images_seq")
public class ClientMainImage extends AbstractEntity {

    @JdbcType(UUIDJdbcType.class)
    @Column(name = "upload_id", nullable = false)
    private UUID uploadId;

    @OneToOne
    @JoinColumn(name = "client_id")
    private ClientProfile client;

    @Column(name = "url")
    private String filename;
}
