package ru.tbank.zaedu.controller;

import java.security.Principal;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.zaedu.DTO.*;
import ru.tbank.zaedu.models.MasterProfile;
import ru.tbank.zaedu.service.MasterService;

@RestController
@RequestMapping("/masters")
public class MasterController extends EntityController<MasterProfile> {

    private final MasterService masterService;

    private static final Class<MasterProfileForMeDTO> MASTER_PROFILE_FOR_ME_DTO_CLASS = MasterProfileForMeDTO.class;
    private static final Class<MasterProfileDTO> MASTER_PROFILE_DTO_CLASS = MasterProfileDTO.class;

    public MasterController(ModelMapper modelMapper, MasterService masterService) {
        super(modelMapper);
        this.masterService = masterService;
    }

    @GetMapping
    public ResponseEntity<MastersListResponseDTO> searchMastersByCategory(@RequestParam String category) {
        MastersListResponseDTO response = masterService.searchMastersByCategory(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MasterProfileDTO> getMasterProfile(Principal principal, @PathVariable Long id) {
        MasterProfileDTO profile = masterService.getMasterProfile(id);
        return ResponseEntity.ok(profile);
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
