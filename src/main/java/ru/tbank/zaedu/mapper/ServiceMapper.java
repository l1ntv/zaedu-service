package ru.tbank.zaedu.mapper;

import org.mapstruct.Mapper;
import ru.tbank.zaedu.DTO.ServiceDTO;
import ru.tbank.zaedu.models.Services;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    ServiceDTO toDto(Services service);

    Services toEntity(ServiceDTO dto);
}
