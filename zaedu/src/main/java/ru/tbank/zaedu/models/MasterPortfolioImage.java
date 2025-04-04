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
@Builder
@Table(name = "master_portfolio_images")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "master_portfolio_images_seq")
public class MasterPortfolioImage extends AbstractEntity {

    @JdbcType(UUIDJdbcType.class)
    @Column(name = "upload_id", nullable = false)
    private UUID uploadId;

    @ManyToOne
    @JoinColumn(name = "master_id")
    private MasterProfile master;

    @Column(name = "url")
    private String filename;

    public MasterPortfolioImage setMaster(MasterProfile master) {
        this.master = master;
        if (master != null && !master.getPortfolioImages().contains(this)) {
            master.getPortfolioImages().add(this);
        }
        return this;
    }
}
