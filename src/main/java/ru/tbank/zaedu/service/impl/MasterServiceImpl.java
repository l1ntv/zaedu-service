package ru.tbank.zaedu.service.impl;

import lombok.RequiredArgsConstructor;
import ru.tbank.zaedu.models.*;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.DTO.MasterProfileDTO;
import ru.tbank.zaedu.DTO.MasterUpdateRequestDTO;
import ru.tbank.zaedu.DTO.MastersListResponseDTO;
import ru.tbank.zaedu.exceptionhandler.ResourceNotFoundException;
import ru.tbank.zaedu.repo.HoodRepository;
import ru.tbank.zaedu.repo.MasterProfileRepository;
import ru.tbank.zaedu.repo.ServiceRepository;
import ru.tbank.zaedu.service.MasterService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterServiceImpl implements MasterService {

    private final MasterProfileRepository masterProfileRepository;
    private final ServiceRepository serviceRepository;
//    private final MasterMapper masterMapper;
    private final HoodRepository hoodRepository;

    @Override
    public MastersListResponseDTO searchMastersByCategory(String category) {
//        List<MasterProfile> masters = masterProfileRepository.findByServices_Name(category);
//        List<MasterProfile> masters = List.of();
//        List<MasterProfileDTO> masterProfiles = masters.stream()
//                .map(masterMapper::toDto)
//                .toList();
//        return new MastersListResponseDTO(masterProfiles, null, null); // Дополните по необходимости

        return null;
    }

    @Override
    public MasterProfileDTO getMasterProfile(Long masterId) {
//        MasterProfile master = masterProfileRepository.findById(masterId)
//                .orElseThrow(() -> new ResourceNotFoundException("Master not found"));
//        return masterMapper.toDto(master);
        return null;
    }

    @Override
    public void updateMasterProfile(Long masterId, MasterUpdateRequestDTO request) {
        MasterProfile master = masterProfileRepository.findById(masterId)
                .orElseThrow(() -> new ResourceNotFoundException("Master not found"));

        // Обновление полей
        master.setDescription(request.getDescription());

        // Обновление фото (преобразуем List<String> в List<MasterPortfolioImage>)
        List<MasterPortfolioImage> portfolioImages = request.getPhotos().stream()
                .map(url -> {
                    MasterPortfolioImage image = new MasterPortfolioImage();
                    image.setMaster(master); // Связь с мастером
                    image.setUrl(url);
                    return image;
                })
                .toList();
        master.setPortfolioImages(portfolioImages); // Используйте существующее поле

//        // Обновление районов (получаем Hood по названиям из DTO)
//        List<Hood> hoods = request.getDistricts().stream()
//                .map(districtName -> hoodRepository.findByHoodName(districtName)
//                        .orElseThrow(() -> new ResourceNotFoundException("Hood not found: " + districtName)))
//                .toList();
//        master.setHoods(hoods); // Используйте существующее поле

        // Обновление услуг
        List<MasterServiceEntity> services = request.getServices().stream()
                .map(serviceDto -> {
                    Services service = serviceRepository.findById(serviceDto.getId().longValue())
                            .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
//                    return new MasterServiceEntity(master, service, serviceDto.getPrice());
                    return new MasterServiceEntity();
                })
                .toList();
        master.setServices(services);

        masterProfileRepository.save(master);
    }
}
