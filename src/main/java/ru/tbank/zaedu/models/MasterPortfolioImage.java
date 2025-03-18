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
@Table(name = "master_portfolio_images")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "master_portfolio_images_seq")
public class MasterPortfolioImage extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "master_id")
    private MasterProfile master;

    private String url;

    public MasterPortfolioImage setMaster(MasterProfile master) {
        this.master = master;
        if (master != null && !master.getPortfolioImages().contains(this)) {
            master.getPortfolioImages().add(this);
        }
        return this;
    }
}
