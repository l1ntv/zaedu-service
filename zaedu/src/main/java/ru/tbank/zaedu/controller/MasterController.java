package ru.tbank.zaedu.controller;

import java.security.Principal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.zaedu.DTO.*;
import ru.tbank.zaedu.models.ClientProfile;
import ru.tbank.zaedu.models.MasterProfile;
import ru.tbank.zaedu.service.ClientService;
import ru.tbank.zaedu.service.FinanceService;
import ru.tbank.zaedu.service.MasterService;

@RestController
@RequestMapping("/masters")
public class MasterController extends EntityController<MasterProfile> {

    private final MasterService masterService;
    private final FinanceService financeService;

    private final ClientService clientService;

    private static final Class<ClientProfileResponseDTO> CLIENT_PROFILE_DTO_CLASS = ClientProfileResponseDTO.class;
    private static final Class<MasterProfileForMeDTO> MASTER_PROFILE_FOR_ME_DTO_CLASS = MasterProfileForMeDTO.class;
    private static final Class<MasterProfileDTO> MASTER_PROFILE_DTO_CLASS = MasterProfileDTO.class;

    public MasterController(ModelMapper modelMapper, MasterService masterService, ClientController clientController, FinanceService financeService, ClientService clientService) {
        super(modelMapper);
        this.masterService = masterService;
        this.financeService = financeService;
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<MastersListResponseDTO> searchMastersByCategory(
            @RequestParam String category, Principal principal) {
        List<MasterProfile> masters = masterService.searchMastersByCategory(category);
        List<MasterProfileDTO> dtos = serialize(masters, MASTER_PROFILE_DTO_CLASS);

        String photoUrl = null;
        Long balance = null;

        if (principal != null) {
            ClientProfile clientProfile = clientService.getClientProfileByName(principal.getName());
            balance = financeService.getUserBalanceByLogin(principal.getName());

            photoUrl = String.valueOf(clientProfile.getMainImage().getUploadId());
        }

        return ResponseEntity.ok(new MastersListResponseDTO(dtos, photoUrl, balance));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MasterProfileDTO> getMasterProfile(@PathVariable Long id) {
        MasterProfile master = masterService.getMasterProfile(id);
        return ResponseEntity.ok(serialize(master, MASTER_PROFILE_DTO_CLASS));
    }

    @GetMapping("/my-public-profile")
    public ResponseEntity<?> getMyPublicProfile(Principal principal) {
        MasterProfile masterProfile = masterService.getMyPublicProfile(principal);
        return ResponseEntity.ok(serialize(masterProfile, MASTER_PROFILE_DTO_CLASS));
    }

    @GetMapping("/my-private-profile")
    public ResponseEntity<?> getMyPrivateProfile(Principal principal) {
        MasterProfile masterProfile = masterService.getMyPrivateProfile(principal);
        return ResponseEntity.ok(serialize(masterProfile, MASTER_PROFILE_FOR_ME_DTO_CLASS));
    }

    @PutMapping("/update-public-profile")
    public ResponseEntity<Void> updateMasterProfileForMe(
            Principal principal, @RequestBody MasterUpdateRequestDTO request) {
        masterService.updateMasterProfile(principal, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-private-profile")
    public ResponseEntity<Void> updateMasterProfileForMe(
            Principal principal, @RequestBody MasterPrivateProfileUpdateRequestDTO request) {
        masterService.updatePrivateProfile(principal, request);
        return ResponseEntity.ok().build();
    }
}
