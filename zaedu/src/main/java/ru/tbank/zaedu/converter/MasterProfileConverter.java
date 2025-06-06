package ru.tbank.zaedu.converter;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.tbank.zaedu.DTO.MasterPrivateProfileUpdateRequestDTO;
import ru.tbank.zaedu.DTO.MasterProfileDTO;
import ru.tbank.zaedu.DTO.MasterProfileForMeDTO;
import ru.tbank.zaedu.DTO.ServiceDTO;
import ru.tbank.zaedu.models.*;

@Component
@RequiredArgsConstructor
public class MasterProfileConverter {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void register() {
        modelMapper.createTypeMap(MasterProfile.class, MasterProfileDTO.class).setPostConverter(getConverter());
        modelMapper.createTypeMap(MasterProfile.class, MasterProfileForMeDTO.class);
        modelMapper.createTypeMap(MasterPrivateProfileUpdateRequestDTO.class, MasterProfile.class);
    }

    Converter<MasterProfile, MasterProfileDTO> getConverter() {
        return it -> {
            var source = it.getSource();
            var destination = it.getDestination();

            destination.setOnlineStatus(source.getIsOnline());
            destination.setPassportVerified(source.getIsConfirmedPassport());
            destination.setContractWork(source.getIsWorkingWithContract());
            destination.setId(Math.toIntExact(source.getUser().getId()));

            // Расчет рейтинга
            double averageRating = Optional.ofNullable(source.getFeedbacks()).orElse(Collections.emptyList()).stream()
                    .mapToInt(MasterFeedback::getEvaluation)
                    .average()
                    .orElse(0.0);
            destination.setAverageRating(averageRating);
            destination.setRatingCount(source.getFeedbacks().size());

            // Преобразование связей
            destination.setServices(source.getServices().stream()
                    .filter(ms -> ms.getServices() != null) // Исключаем null-значения
                    .map(ms -> new ServiceDTO(
                            ms.getServices().getId(), ms.getServices().getName(), ms.getPrice()))
                    .collect(Collectors.toList()));

            destination.setDistricts(source.getHoods().stream()
                    .map(MasterHoodsEntity::getHood) // Получаем объект Hood из MasterHoodsEntity
                    .map(Hood::getName) // Получаем имя района
                    .collect(Collectors.toList()));

            destination.setPhotos(source.getPortfolioImages().stream()
                    .map(MasterPortfolioImage::getUploadId)
                    .filter(Objects::nonNull)
                    .map(UUID::toString)
                    .collect(Collectors.toList()));

            if (source.getMainImage() != null && source.getMainImage().getFilename() != null) {
                destination.setPersonalPhoto(String.valueOf(source.getMainImage().getUploadId()));
            } else {
                destination.setPersonalPhoto(null);
            }

            return destination;
        };
    }
}
