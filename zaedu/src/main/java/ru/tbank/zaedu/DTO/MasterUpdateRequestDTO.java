package ru.tbank.zaedu.DTO;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MasterUpdateRequestDTO {
    private String description;
    private List<FileResponseDto> photos;
    private List<ServiceDTO> services;
    private List<String> districts;
    private String filename;
    private UUID uuid;
}
