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
@Table(name = "master_main_images")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "master_main_images_seq")
public class MasterMainImage extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "master_id")
    private MasterProfile master;

    private String url;
}
