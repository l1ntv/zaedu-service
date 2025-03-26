package ru.tbank.zaedu.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.zaedu.DTO.ClientProfileRequestDTO;
import ru.tbank.zaedu.DTO.ClientProfileResponseDTO;
import ru.tbank.zaedu.models.ClientProfile;
import ru.tbank.zaedu.service.ClientService;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/clients")
public class ClientController extends EntityController<ClientProfile> {

    private final ClientService clientService;

    private static final Class<ClientProfileResponseDTO> CLIENT_PROFILE_DTO_CLASS = ClientProfileResponseDTO.class;

    public ClientController(ModelMapper modelMapper, ClientService clientService) {
        super(modelMapper);
        this.clientService = clientService;
    }

    @GetMapping("/my-profile")
    public ResponseEntity<?> getClientProfileInfo(Principal principal) {
        ClientProfile clientProfile = clientService.getClientProfileByName(principal.getName());
        return ResponseEntity.ok(serialize(clientProfile, CLIENT_PROFILE_DTO_CLASS));
    }

    @PutMapping("/update")
    private ResponseEntity<Optional<ClientProfileRequestDTO>> updateClientProfile(Principal principal,
                                                                                  @RequestBody ClientProfileRequestDTO requestDTO) {
        clientService.updateClientProfile(principal.getName(), requestDTO);
        return ResponseEntity.ok().build();
    }
}
