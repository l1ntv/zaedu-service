package ru.tbank.zaedu.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.tbank.zaedu.DTO.ServiceDTO;
import ru.tbank.zaedu.models.*;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.DTO.MasterProfileDTO;
import ru.tbank.zaedu.DTO.MasterUpdateRequestDTO;
import ru.tbank.zaedu.DTO.MastersListResponseDTO;
import ru.tbank.zaedu.exceptionhandler.ResourceNotFoundException;
import ru.tbank.zaedu.repo.HoodRepository;
import ru.tbank.zaedu.repo.MasterProfileRepository;
import ru.tbank.zaedu.repo.ServiceRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterServiceImpl implements MasterService {

    private final MasterProfileRepository masterProfileRepository;
    private final ServiceRepository serviceRepository;
    private final HoodRepository hoodRepository;
    private final ModelMapper modelMapper; // Добавьте ModelMapper

    @Override
    public MastersListResponseDTO searchMastersByCategory(String category) {
        List<MasterProfile> masters = masterProfileRepository.findByServiceCategory(category);
        List<MasterProfileDTO> dtos = masters.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new MastersListResponseDTO(dtos, null, null);
    }

    @Override
    public MasterProfileDTO getMasterProfile(Long masterId) {
        MasterProfile master = masterProfileRepository.findById(masterId)
                .orElseThrow(() -> new ResourceNotFoundException("Master not found"));
        return convertToDTO(master);
    }

    @Override
    @Transactional
    public void updateMasterProfile(Long masterId, MasterUpdateRequestDTO request) {
        MasterProfile master = masterProfileRepository.findById(masterId)
                .orElseThrow(() -> new ResourceNotFoundException("Master not found"));

        // Обновление основных полей
        master.setDescription(request.getDescription());

        // Обновление услуг
        List<MasterServiceEntity> services = request.getServices().stream()
                .map(dto -> {
                    Services service = serviceRepository.findByName(dto.getServiceName())
                            .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
                    return new MasterServiceEntity()
                            .setMaster(master)
                            .setServices(service)
                            .setPrice(dto.getCost());
                })
                .collect(Collectors.toList());
        master.setServices(services);

        // Обновление районов
        List<Hood> hoods = request.getDistricts().stream()
                .map(district -> hoodRepository.findByName(district)
                        .orElseThrow(() -> new ResourceNotFoundException("Hood not found")))
                .collect(Collectors.toList());

        // Проверяем существующие связи и добавляем только новые
        for (Hood hood : hoods) {
            if (!master.getHoods().contains(hood)) {
                master.getHoods().add(hood);
            }
        }

        // Обновление портфолио
        List<MasterPortfolioImage> portfolio = request.getPhotos().stream()
                .map(url -> new MasterPortfolioImage()
                        .setMaster(master)
                        .setUrl(url))
                .collect(Collectors.toList());

        master.getPortfolioImages().addAll(portfolio); // Добавляем новые изображения

        masterProfileRepository.save(master);
    }

    private MasterProfileDTO convertToDTO(MasterProfile master) {

        MasterProfileDTO dto = modelMapper.map(master, MasterProfileDTO.class);

        // Расчет рейтинга
        double averageRating = Optional.ofNullable(master.getFeedbacks())
                .orElse(Collections.emptyList()).stream()
                .mapToInt(MasterFeedback::getEvaluation)
                .average()
                .orElse(0.0);
        dto.setAverageRating(averageRating);
        dto.setRatingCount(master.getFeedbacks().size());

        // Преобразование связей
        dto.setServices(master.getServices().stream()
                .filter(ms -> ms.getServices() != null) // Исключаем null-значения
                .map(ms -> new ServiceDTO(
                        ms.getServices().getId(),
                        ms.getServices().getName(),
                        ms.getPrice()))
                .collect(Collectors.toList()));

        dto.setDistricts(master.getHoods().stream()
                .map(Hood::getName)
                .collect(Collectors.toList()));

        dto.setPhotos(master.getPortfolioImages().stream()
                .map(MasterPortfolioImage::getUrl)
                .collect(Collectors.toList()));

        if (Optional.ofNullable(master.getMainImages())
                .filter(images -> !images.isEmpty())
                .isPresent()) {
            dto.setPersonalPhoto(master.getMainImages().get(0).getUrl());
        }

        return dto;
    }
}
