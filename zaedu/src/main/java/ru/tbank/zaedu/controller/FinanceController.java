package ru.tbank.zaedu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.zaedu.DTO.FinanceOperationRequest;
import ru.tbank.zaedu.service.FinanceService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService financeService;

    @PatchMapping("/withdrawal")
    public ResponseEntity<Void> withdrawalMoney(@RequestBody FinanceOperationRequest financeOperationRequest, Principal principal) {
        String login = principal.getName();
        financeService.withdrawalMoney(financeOperationRequest.getMoneyCount(), login);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/replenishment")
    public ResponseEntity<Void> replenishmentMoney(@RequestBody FinanceOperationRequest financeOperationRequest, Principal principal) {
        String login = principal.getName();
        financeService.replenishmentMoney(financeOperationRequest.getMoneyCount(), login);
        return ResponseEntity.ok().build();
    }
}
