package ru.tbank.zaedu.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class MastersListResponseDTO {
    private List<MasterProfileDTO> masters;
    private String photoUrl;
    private Integer balance;
}
