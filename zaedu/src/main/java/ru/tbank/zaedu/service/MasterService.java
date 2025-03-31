package src.main.java.ru.tbank.zaedu.service;

import java.security.Principal;
import java.util.List;
import src.main.java.ru.tbank.zaedu.DTO.MasterPrivateProfileUpdateRequestDTO;
import src.main.java.ru.tbank.zaedu.DTO.MasterUpdateRequestDTO;
import src.main.java.ru.tbank.zaedu.models.MasterProfile;

public interface MasterService {
    List<MasterProfile> searchMastersByCategory(String category);

    MasterProfile getMasterProfile(Long masterId);

    void updateMasterProfile(Principal principal, MasterUpdateRequestDTO request);

    void updatePrivateProfile(Principal principal, MasterPrivateProfileUpdateRequestDTO request);

    MasterProfile getMyPublicProfile(Principal principal);

    MasterProfile getMyPrivateProfile(Principal principal);
}
