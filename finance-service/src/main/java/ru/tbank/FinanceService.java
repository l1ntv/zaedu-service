package ru.tbank;

import org.springframework.stereotype.Service;

@Service
public class FinanceService {

    private final KafkaProducer kafkaProducer;

    public FinanceService(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public boolean isFinanceOperationCorrect(Long balance, Long count, OperationType operationType) {
        if (this.isInputFinanceOperationDataCorrect(balance, count)) {
            if (operationType.equals(OperationType.REPLENISHMENT)) return true;
            if (operationType.equals(OperationType.WITHDRAWAL)
                    && this.isBalanceGreaterOrEqualsThenFinanceOperationCount(balance, count)) {
                return true;
            }
        }
        return false;
    }

    public Long getNewBalance(Long balance, Long count, OperationType operationType) {
        if (operationType.equals(OperationType.REPLENISHMENT)) return balance + count;
        return balance - count;
    }

    public void sendValidationResult(Long id, Long newBalance, boolean isOperationResultSuccess) {
        kafkaProducer.sendFinanceOperationResult(id, newBalance, isOperationResultSuccess);
    }

    private boolean isInputFinanceOperationDataCorrect(Long balance, Long count) {
        return (balance >= 0L && count > 0);
    }

    private boolean isBalanceGreaterOrEqualsThenFinanceOperationCount(Long balance, Long count) {
        return balance >= count;
    }
}
