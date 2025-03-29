package ru.tbank.zaedu.service;

import java.security.Principal;
import java.util.List;
import ru.tbank.zaedu.DTO.MasterPrivateProfileUpdateRequestDTO;
import ru.tbank.zaedu.DTO.MasterUpdateRequestDTO;
import ru.tbank.zaedu.models.MasterProfile;

public interface MasterService {
    List<MasterProfile> searchMastersByCategory(String category);

    MasterProfile getMasterProfile(Long masterId);

    void updateMasterProfile(Principal principal, MasterUpdateRequestDTO request);

    void updatePrivateProfile(Principal principal, MasterPrivateProfileUpdateRequestDTO request);

    MasterProfile getMyPublicProfile(Principal principal);

    MasterProfile getMyPrivateProfile(Principal principal);
}
