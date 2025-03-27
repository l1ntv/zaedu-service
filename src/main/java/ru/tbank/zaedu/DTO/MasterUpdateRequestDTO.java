package ru.tbank.zaedu.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MasterUpdateRequestDTO {
    private String description;
    private String personalPhoto;
    private List<String> photos; // URLs of photos
    private List<ServiceDTO> services;
    private List<String> districts;
}
