package ru.tbank.zaedu.DTO;

import java.util.List;
import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MasterProfileDTO {
    private Integer id;
    private String fullName;
    private String description;
    private Boolean onlineStatus;
    private List<String> photos; // URLs of photos
    private String personalPhoto; // URL of personal photo
    private Double averageRating;
    private Integer ratingCount;
    private Boolean passportVerified;
    private Boolean contractWork;
    private List<ServiceDTO> services;
    private List<String> districts;
    private List<ReportDTO> reports;
}
