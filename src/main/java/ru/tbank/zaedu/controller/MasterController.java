package ru.tbank.zaedu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.zaedu.DTO.MasterProfileDTO;
import ru.tbank.zaedu.DTO.MasterUpdateRequestDTO;
import ru.tbank.zaedu.DTO.MastersListResponseDTO;
import ru.tbank.zaedu.service.MasterService;

@RestController
@RequestMapping("/masters")
@RequiredArgsConstructor
public class MasterController {

    private final MasterService masterService;

    @GetMapping
    public ResponseEntity<MastersListResponseDTO> searchMastersByCategory(
            @RequestParam String category) {
        MastersListResponseDTO response = masterService.searchMastersByCategory(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MasterProfileDTO> getMasterProfile(@PathVariable Long id) {
        MasterProfileDTO profile = masterService.getMasterProfile(id);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Void> updateMasterProfile(
            @PathVariable Long id,
            @RequestBody MasterUpdateRequestDTO request) {
        masterService.updateMasterProfile(id, request);
        return ResponseEntity.ok().build();
    }
}
