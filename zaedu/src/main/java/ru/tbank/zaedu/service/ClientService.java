package src.main.java.ru.tbank.zaedu.service;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import src.main.java.ru.tbank.zaedu.DTO.ClientProfileRequestDTO;
import src.main.java.ru.tbank.zaedu.models.*;
import src.main.java.ru.tbank.zaedu.repo.ClientMainImageRepository;
import src.main.java.ru.tbank.zaedu.repo.ClientProfileRepository;
import src.main.java.ru.tbank.zaedu.repo.UserRepository;

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

    public Optional<ClientProfile> getClientProfileById(long clientId) {
        return clientProfileRepository.findById(clientId);
    }

    public void updateClientProfile(String name, ClientProfileRequestDTO requestDTO) {
        Optional<User> user = userRepository.findByLogin(name);
        ClientProfile clientProfile =
                clientProfileRepository.findById(user.get().getId()).get();

        modelMapper.map(requestDTO, clientProfile);

        if (requestDTO.getMainImage() != null && Objects.isNull(clientProfile.getMainImage())) {
            ClientMainImage clientMainImage = new ClientMainImage(clientProfile, requestDTO.getMainImage());
            clientMainImageRepository.save(clientMainImage);
            clientProfile.setMainImage(clientMainImage);
        }

        clientProfileRepository.save(clientProfile);
    }
}
