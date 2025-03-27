package ru.tbank.zaedu.service;

import java.security.Principal;
import ru.tbank.zaedu.DTO.MasterProfileDTO;
import ru.tbank.zaedu.DTO.MasterUpdateRequestDTO;
import ru.tbank.zaedu.DTO.MastersListResponseDTO;
import ru.tbank.zaedu.models.MasterProfile;

public interface MasterService {
    MastersListResponseDTO searchMastersByCategory(String category);

    MasterProfileDTO getMasterProfile(Long masterId);

    void updateMasterProfile(Principal principal, MasterUpdateRequestDTO request);

    MasterProfile getMyPublicProfile(Principal principal);

    MasterProfile getMyPrivateProfile(Principal principal);
}
