package ru.tbank.zaedu.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@Getter
@Setter
public class MasterUpdateRequestDTO {
    private String description;
    private List<String> photos; // URLs of photos
    private List<ServiceDTO> services;
    private List<String> districts;


}
