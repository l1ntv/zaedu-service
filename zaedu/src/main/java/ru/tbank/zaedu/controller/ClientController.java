package ru.tbank.zaedu.controller;

import java.security.Principal;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.tbank.zaedu.DTO.ClientProfileRequestDTO;
import ru.tbank.zaedu.DTO.ClientProfileResponseDTO;
import ru.tbank.zaedu.models.ClientProfile;
import ru.tbank.zaedu.service.ClientService;
import ru.tbank.zaedu.service.FinanceService;

@RestController
@RequestMapping("/clients")
public class ClientController extends EntityController<ClientProfile> {

    private final ClientService clientService;
    private final FinanceService financeService;

    private static final Class<ClientProfileResponseDTO> CLIENT_PROFILE_DTO_CLASS = ClientProfileResponseDTO.class;

    public ClientController(ModelMapper modelMapper, ClientService clientService, FinanceService financeService) {
        super(modelMapper);
        this.clientService = clientService;
        this.financeService = financeService;
    }

    @GetMapping("/my-profile")
    public ResponseEntity<?> getClientProfileInfo(Principal principal) {
        ClientProfile clientProfile = clientService.getClientProfileByName(principal.getName());
        Long balance = financeService.getUserBalanceByLogin(principal.getName());
        ClientProfileResponseDTO сlientProfileResponseDTO = serialize(clientProfile, CLIENT_PROFILE_DTO_CLASS);
        сlientProfileResponseDTO.setBalance(balance);
        return ResponseEntity.ok(сlientProfileResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClientProfileInfoByID(@PathVariable Long id) {
        ClientProfile clientProfile = clientService
                .getClientProfileById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Long balance = financeService.getUserBalanceById(id);
        ClientProfileResponseDTO сlientProfileResponseDTO = serialize(clientProfile, CLIENT_PROFILE_DTO_CLASS);
        сlientProfileResponseDTO.setBalance(balance);
        return ResponseEntity.ok(сlientProfileResponseDTO);
    }

    @PutMapping("/update-profile")
    private ResponseEntity<Optional<ClientProfileRequestDTO>> updateClientProfile(
            Principal principal, @RequestBody ClientProfileRequestDTO requestDTO) {
        clientService.updateClientProfile(principal.getName(), requestDTO);
        return ResponseEntity.ok().build();
    }

    public ClientProfileResponseDTO getClientProfileResponseDTO(Principal principal) {
        ClientProfile clientProfile = clientService.getClientProfileByName(principal.getName());
        Long balance = financeService.getUserBalanceByLogin(principal.getName());
        ClientProfileResponseDTO сlientProfileResponseDTO = serialize(clientProfile, CLIENT_PROFILE_DTO_CLASS);
        сlientProfileResponseDTO.setBalance(balance);
        return сlientProfileResponseDTO;
    }
}
