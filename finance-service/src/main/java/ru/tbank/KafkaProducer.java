package ru.tbank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendFinanceOperationResult(Long id, Long newBalance, boolean isOperationResultSuccess) {
        // id;newBalance;isOperationResultSuccess
        String message = id + ";" + newBalance + ";" + (isOperationResultSuccess ? "1" : "0");
        kafkaTemplate.send("finance-service-response", message);
    }
}
