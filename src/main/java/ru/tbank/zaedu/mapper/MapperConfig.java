package ru.tbank.zaedu.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tbank.zaedu.DTO.MasterProfileDTO;
import ru.tbank.zaedu.DTO.ServiceDTO;
import ru.tbank.zaedu.models.MasterProfile;
import ru.tbank.zaedu.models.Hood;
import ru.tbank.zaedu.models.MasterPortfolioImage;
import ru.tbank.zaedu.models.MasterFeedback;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setImplicitMappingEnabled(false); // Отключаем автоматический маппинг

        // Настройка маппинга для MasterProfile
        TypeMap<MasterProfile, MasterProfileDTO> typeMap = mapper.createTypeMap(MasterProfile.class, MasterProfileDTO.class);

        typeMap.addMappings(map -> {
            map.map(MasterProfile::getId, MasterProfileDTO::setId);
            map.map(MasterProfile::getFullName,
                    MasterProfileDTO::setFullName);
            map.map(MasterProfile::getDescription, MasterProfileDTO::setDescription);
            map.map(MasterProfile::getIsOnline, MasterProfileDTO::setOnlineStatus);
            map.map(MasterProfile::getIsConfirmedPassport, MasterProfileDTO::setPassportVerified);
            map.map(MasterProfile::getIsWorkingWithContract, MasterProfileDTO::setContractWork);

            // Игнорирование коллекций и связанных объектов
            map.skip(MasterProfileDTO::setServices);
            map.skip(MasterProfileDTO::setDistricts);
            map.skip(MasterProfileDTO::setPhotos);
            map.skip(MasterProfileDTO::setPersonalPhoto);
            map.skip(MasterProfileDTO::setAverageRating);
            map.skip(MasterProfileDTO::setRatingCount);
            map.skip(MasterProfileDTO::setReports); // Игнорируем поле reports
        });

        return mapper;
    }
}
