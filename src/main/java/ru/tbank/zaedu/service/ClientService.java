package ru.tbank.zaedu.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.DTO.ClientProfileRequestDTO;
import ru.tbank.zaedu.models.*;
import ru.tbank.zaedu.repo.ClientMainImageRepository;
import ru.tbank.zaedu.repo.ClientProfileRepository;
import ru.tbank.zaedu.repo.UserRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientProfileRepository clientProfileRepository;

    private final ClientMainImageRepository clientMainImageRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public ClientProfile getClientProfileByName(String name) {
        Optional<User> user = userRepository.findByLogin(name);
        Optional<ClientProfile> clientProfileOptional = clientProfileRepository.findByUser(user.get());
        return clientProfileOptional.get();
    }

    public void updateClientProfile(String name, ClientProfileRequestDTO requestDTO) {
        Optional<User> user = userRepository.findByLogin(name);
        ClientProfile clientProfile = clientProfileRepository.findById(user.get().getId()).get();

        modelMapper.map(requestDTO, clientProfile);

        if (requestDTO.getMainImage() != null && Objects.isNull(clientProfile.getMainImage())) {
            ClientMainImage clientMainImage = new ClientMainImage(clientProfile, requestDTO.getMainImage());
            clientMainImageRepository.save(clientMainImage);
            clientProfile.setMainImage(clientMainImage);
        }

        clientProfileRepository.save(clientProfile);
    }
}
