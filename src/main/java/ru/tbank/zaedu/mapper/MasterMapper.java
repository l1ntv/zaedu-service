package ru.tbank.zaedu.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.tbank.zaedu.DTO.MasterProfileDTO;
import ru.tbank.zaedu.models.MasterProfile;

//@Mapper(componentModel = "spring", uses = {ServiceMapper.class})
//public interface MasterMapper {
//
//    @Mapping(target = "photos", source = "portfolioImages")
//    @Mapping(target = "districts", source = "hoods")
//    MasterProfileDTO toDto(MasterProfile master);
//
//    @Mapping(target = "portfolioImages", ignore = true) // Или реализуйте обратное преобразование
//    @Mapping(target = "hoods", ignore = true) // Или реализуйте обратное преобразование
//    MasterProfile toEntity(MasterProfileDTO dto);
//}
