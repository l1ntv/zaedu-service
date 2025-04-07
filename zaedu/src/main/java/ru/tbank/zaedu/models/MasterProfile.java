package ru.tbank.zaedu.models;

import static ru.tbank.zaedu.models.AbstractEntity.DEFAULT_GENERATOR;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
@Table(name = "master_profile")
@SequenceGenerator(name = DEFAULT_GENERATOR, sequenceName = "master_profile_seq")
public class MasterProfile extends AbstractEntity {
    private String surname;
    private String name;
    private String patronymic;
    private String email;
    private String telephoneNumber;
    private Boolean isCompany;
    private Boolean isConfirmedPassport;
    private String passportSeries;
    private String passportNumber;
    private LocalDate createDate;
    private String profileName;
    private String description;
    private Boolean isOnline;
    private Boolean isWorkingWithContract;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @OneToOne
    @JoinColumn(name = "master_id")
    private User user;

    @OneToMany(mappedBy = "master", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<MasterServiceEntity> services = new ArrayList<>();

    @OneToMany(mappedBy = "master")
    @ToString.Exclude
    private List<MasterFeedback> feedbacks;

    @OneToMany(mappedBy = "master", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<MasterHoodsEntity> hoods = new ArrayList<>();

    public void addHood(Hood hood) {
        MasterHoodsEntity masterHood =
                MasterHoodsEntity.builder().master(this).hood(hood).build();
        hoods.add(masterHood);
        hood.getMasters().add(masterHood); // Если в Hood есть обратная связь
    }

    @OneToMany(mappedBy = "master")
    @ToString.Exclude
    private List<Order> orders;

    @OneToOne(mappedBy = "master", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private MasterMainImage mainImage;

    @OneToMany(mappedBy = "master", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<MasterPortfolioImage> portfolioImages = new ArrayList<>();

    public String getFullName() {
        return Stream.of(surname, name, patronymic).filter(Objects::nonNull).collect(Collectors.joining(" "));
    }
}
