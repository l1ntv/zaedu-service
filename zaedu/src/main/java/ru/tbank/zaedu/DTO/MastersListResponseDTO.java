package ru.tbank.zaedu.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MastersListResponseDTO {
    private List<MasterProfileDTO> masters;
    private String photoUrl;
    private Long balance;
}
