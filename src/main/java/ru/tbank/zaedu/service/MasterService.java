package ru.tbank.zaedu.service;

import ru.tbank.zaedu.DTO.MasterProfileDTO;
import ru.tbank.zaedu.DTO.MasterUpdateRequestDTO;
import ru.tbank.zaedu.DTO.MastersListResponseDTO;

public interface MasterService {
    MastersListResponseDTO searchMastersByCategory(String category);
    MasterProfileDTO getMasterProfile(Long masterId);
    void updateMasterProfile(Long masterId, MasterUpdateRequestDTO request);
}
