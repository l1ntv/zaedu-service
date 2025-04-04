package ru.tbank.zaedu.service;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.zaedu.DTO.ClientProfileRequestDTO;
import ru.tbank.zaedu.exception.ResourceNotFoundException;
import ru.tbank.zaedu.models.*;
import ru.tbank.zaedu.repo.ClientMainImageRepository;
import ru.tbank.zaedu.repo.ClientProfileRepository;
import ru.tbank.zaedu.repo.UserRepository;
import ru.tbank.zaedu.service.file.FileService;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientProfileRepository clientProfileRepository;

    private final ClientMainImageRepository clientMainImageRepository;

    private final UserRepository userRepository;

    private final FileService fileService;

    private final ModelMapper modelMapper;

    public ClientProfile getClientProfileByName(String name) {
        Optional<User> user = userRepository.findByLogin(name);
        Optional<ClientProfile> clientProfileOptional = clientProfileRepository.findByUser(user.get());
        return clientProfileOptional.get();
    }

    public Optional<ClientProfile> getClientProfileById(long clientId) {
        return clientProfileRepository.findById(clientId);
    }

    @Transactional
    public void updateClientProfile(String name, ClientProfileRequestDTO requestDTO) {
        Optional<User> user = userRepository.findByLogin(name);
        ClientProfile clientProfile =
                clientProfileRepository.findById(user.get().getId()).get();

        modelMapper.map(requestDTO, clientProfile);

        if (requestDTO.getFilename() != null && requestDTO.getUuid() != null
                && Objects.isNull(clientProfile.getMainImage())) {

            ClientMainImage clientMainImage = new ClientMainImage
                    (requestDTO.getUuid(), clientProfile, requestDTO.getFilename());

            clientMainImageRepository.save(clientMainImage);
            clientProfile.setMainImage(clientMainImage);
        } else if (requestDTO.getFilename() != null && requestDTO.getUuid() != null) {

            fileService.delete(clientProfile.getMainImage().getFilename());
            clientProfile.getMainImage().setFilename(requestDTO.getFilename());
            clientProfile.getMainImage().setUploadId(requestDTO.getUuid());
        } else if (requestDTO.getFilename() == null && requestDTO.getUuid() == null
                && Objects.nonNull(clientProfile.getMainImage())) {

            fileService.delete(clientProfile.getMainImage().getFilename());
            clientProfile.setMainImage(null);
        }

        clientProfileRepository.save(clientProfile);
    }
}
