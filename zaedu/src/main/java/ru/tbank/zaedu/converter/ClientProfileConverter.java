package ru.tbank.zaedu.converter;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.tbank.zaedu.DTO.ClientProfileRequestDTO;
import ru.tbank.zaedu.DTO.ClientProfileResponseDTO;
import ru.tbank.zaedu.models.City;
import ru.tbank.zaedu.models.ClientProfile;
import ru.tbank.zaedu.repo.CityRepository;

import static ru.tbank.zaedu.config.AppConstants.BASE_IMAGE_URL;

@Component
@RequiredArgsConstructor
public class ClientProfileConverter {
    private static final Long DEFAULT_BALANCE = 0L;

    private final ModelMapper modelMapper;

    private final CityRepository cityRepository;

    @PostConstruct
    public void register() {
        modelMapper
                .createTypeMap(ClientProfileRequestDTO.class, ClientProfile.class)
                .setPostConverter(getConverter());

        modelMapper
                .createTypeMap(ClientProfile.class, ClientProfileResponseDTO.class)
                .setPostConverter(getConverter1());
    }

    Converter<ClientProfileRequestDTO, ClientProfile> getConverter() {
        return it -> {
            var source = it.getSource();
            var destination = it.getDestination();

            if (source.getCity() != null) {
                City city = cityRepository
                        .findByName(source.getCity())
                        .orElseThrow(() -> new RuntimeException("Город не найден"));
                destination.setCity(city);
            }

            return destination;
        };
    }

    Converter<ClientProfile, ClientProfileResponseDTO> getConverter1() {
        return it -> {
            var source = it.getSource();
            var destination = it.getDestination();

            if (source.getCity() != null) {
                destination.setCity(source.getCity().getName());
            }

            if (source.getMainImage().getFilename() != null) {
                destination.setPhotoUrl(BASE_IMAGE_URL + source.getMainImage().getFilename());
            } else {
                destination.setPhotoUrl(null);
            }

            destination.setBalance(DEFAULT_BALANCE);

            return destination;
        };
    }
}
