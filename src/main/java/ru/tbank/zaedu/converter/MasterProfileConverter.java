package ru.tbank.zaedu.converter;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.tbank.zaedu.DTO.MasterProfileDTO;
import ru.tbank.zaedu.models.MasterProfile;

@Component
@RequiredArgsConstructor
public class MasterProfileConverter {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void register() {
        modelMapper
                .createTypeMap(MasterProfile.class, MasterProfileDTO.class)
                .setPostConverter(getConverter());
    }

    Converter<MasterProfile, MasterProfileDTO> getConverter() {
        return it -> {
            var source = it.getSource();
            var destination = it.getDestination();

            destination.setOnlineStatus(source.getIsOnline());
            destination.setPassportVerified(source.getIsConfirmedPassport());
            destination.setContractWork(source.getIsWorkingWithContract());

            return destination;
        };
    }
}
