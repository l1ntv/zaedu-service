package ru.tbank.zaedu.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.repo.FinanceBalanceRepository;

@Service
@RequiredArgsConstructor
public class FinanceServiceKafkaConsumer {

    private final FinanceBalanceRepository financeBalanceRepository;

    @KafkaListener(topics = "finance-service-response", groupId = "zaedu-group")
    public void listen(String message) {
        String[] parts = message.split(";");
        if (parts.length == 3) {
            Long id = Long.parseLong(parts[0]);
            Long balance = Long.parseLong(parts[1]);
            boolean isOperationResultSuccess = "1".equals(parts[2]);
            if (isOperationResultSuccess) {
                financeBalanceRepository
                        .findByUser_Id(id)
                        .ifPresent(financeBalance -> {
                            financeBalance.setBalance(balance);
                            financeBalanceRepository.save(financeBalance);
                        });
            }
        }
    }
}
