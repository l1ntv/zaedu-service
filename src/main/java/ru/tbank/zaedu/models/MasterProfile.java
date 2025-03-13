package ru.tbank.zaedu.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.tbank.zaedu.models.AbstractEntity.DEFAULT_GENERATOR;

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

    @OneToMany(mappedBy = "master")
    @ToString.Exclude
    private List<MasterServiceEntity> services;

    @OneToMany(mappedBy = "master")
    @ToString.Exclude
    private List<MasterFeedback> feedbacks;

    @ManyToMany
    @JoinTable(
            name = "master_hoods",
            joinColumns = @JoinColumn(name = "master_id"),
            inverseJoinColumns = @JoinColumn(name = "hood_id")
    )
    @ToString.Exclude
    private List<Hood> hoods = new ArrayList<>();

    @OneToMany(mappedBy = "master")
    @ToString.Exclude
    private List<Order> orders;

    @OneToMany(mappedBy = "master")
    @ToString.Exclude
    private List<MasterMainImage> mainImages;

    @OneToMany(mappedBy = "master", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<MasterPortfolioImage> portfolioImages = new ArrayList<>();
}
