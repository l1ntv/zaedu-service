package ru.tbank.zaedu.converter;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.tbank.zaedu.DTO.OrderClientDTO;
import ru.tbank.zaedu.DTO.OrderMasterDTO;
import ru.tbank.zaedu.models.*;

@Component
@RequiredArgsConstructor
public class OrderConverter {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void register() {
        modelMapper.createTypeMap(Order.class, OrderClientDTO.class).setPostConverter(getConverter());
        modelMapper.createTypeMap(Order.class, OrderMasterDTO.class).setPostConverter(getConverter1());
    }

    Converter<Order, OrderClientDTO> getConverter() {
        return it -> {
            var source = it.getSource();
            var destination = it.getDestination();

            // Извлечение serviceType из Services
            if (source.getServices() != null) {
                destination.setServiceType(source.getServices().getName());
            } else {
                destination.setServiceType(null); // Если сервис не указан
            }

            // Извлечение phone из ClientProfile
            if (source.getClient() != null && source.getMaster().getTelephoneNumber() != null) {
                destination.setPhoneMaster(source.getClient().getTelephoneNumber());
            } else {
                destination.setPhoneMaster(null); // Если телефон не указан
            }

            return destination;
        };
    }

    Converter<Order, OrderMasterDTO> getConverter1() {
        return it -> {
            var source = it.getSource();
            var destination = it.getDestination();

            // Извлечение serviceType из Services
            if (source.getServices() != null) {
                destination.setServiceType(source.getServices().getName());
            } else {
                destination.setServiceType(null); // Если сервис не указан
            }

            // Извлечение phone из ClientProfile
            if (source.getClient() != null && source.getClient().getTelephoneNumber() != null) {
                destination.setPhoneClient(source.getClient().getTelephoneNumber());
            } else {
                destination.setPhoneClient(null); // Если телефон не указан
            }

            return destination;
        };
    }
}
