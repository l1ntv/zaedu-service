package src.main.java.ru.tbank.zaedu.controller;

import java.security.Principal;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import src.main.java.ru.tbank.zaedu.DTO.ClientProfileRequestDTO;
import src.main.java.ru.tbank.zaedu.DTO.ClientProfileResponseDTO;
import src.main.java.ru.tbank.zaedu.models.ClientProfile;
import src.main.java.ru.tbank.zaedu.service.ClientService;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getClientProfileInfoByID(@PathVariable Long id) {
        ClientProfile clientProfile = clientService
                .getClientProfileById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(serialize(clientProfile, CLIENT_PROFILE_DTO_CLASS));
    }

    @PutMapping("/update-profile")
    private ResponseEntity<Optional<ClientProfileRequestDTO>> updateClientProfile(
            Principal principal, @RequestBody ClientProfileRequestDTO requestDTO) {
        clientService.updateClientProfile(principal.getName(), requestDTO);
        return ResponseEntity.ok().build();
    }

    public ClientProfileResponseDTO getClientProfileResponseDTO(Principal principal) {
        ClientProfile clientProfile = clientService.getClientProfileByName(principal.getName());
        return serialize(clientProfile, CLIENT_PROFILE_DTO_CLASS);
    }
}
