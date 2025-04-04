package ru.tbank;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private final FinanceService financeService;

    public KafkaConsumer(FinanceService financeService) {
        this.financeService = financeService;
    }

    @KafkaListener(topics = "finance-service-request", groupId = "finance-service-group")
    public void listen(String message) {
        // id;balance;operationType;count
        String[] parts = message.split(";");
        if (parts.length == 4) {
            Long id = Long.parseLong(parts[0]);
            Long balance = Long.parseLong(parts[1]);
            OperationType operationType = OperationType.valueOf(parts[2]);
            Long count = Long.parseLong(parts[3]);
            boolean isOperationResultSuccess = financeService.isFinanceOperationCorrect(balance, count, operationType);
            balance = financeService.getNewBalance(balance, count, operationType);
            financeService.sendValidationResult(id, balance, isOperationResultSuccess);
        }
    }
}
