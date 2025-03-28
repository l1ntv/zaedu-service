package ru.tbank.zaedu.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.zaedu.DTO.MasterProfileDTO;
import ru.tbank.zaedu.DTO.MasterUpdateRequestDTO;
import ru.tbank.zaedu.DTO.MastersListResponseDTO;
import ru.tbank.zaedu.service.MasterService;

import java.security.Principal;

@RestController
@RequestMapping("/masters")
public class MasterController {

    private final MasterService masterService;

    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

    @GetMapping
    public ResponseEntity<MastersListResponseDTO> searchMastersByCategory(
            @RequestParam String category) {
        MastersListResponseDTO response = masterService.searchMastersByCategory(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MasterProfileDTO> getMasterProfile(Principal principal, @PathVariable Long id) {
        MasterProfileDTO profile = masterService.getMasterProfile(id);
        return ResponseEntity.ok(profile);
    }

    // Сделать ручку для просмотра своего профиля for other от лица мастера
    // public ResponseEntity<MasterProfileDTO> getMasterProfileForOther(Principal principal) {

    // Сделать ручку для просмотра своего профиля for me от лица мастера
    // public ResponseEntity<MasterProfileDTO> getMasterProfileForMe(Principal principal) {


    @PutMapping("/update") // Разделить эту ручку на 2: updateMasterProfileForMe и updateMasterProfileForOther
    public ResponseEntity<Void> updateMasterProfileForMe(
            Principal principal,
            @RequestBody MasterUpdateRequestDTO request) {
        masterService.updateMasterProfile(principal, request);
        return ResponseEntity.ok().build();
    }

    // Сделать ручку для просмотра своих заказов, возвращать отсортированный список заказов по status
    // вытаскивать данные по Principal
}
