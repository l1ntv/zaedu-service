package ru.tbank;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private final PassportValidationService validationService;

    public KafkaConsumer(PassportValidationService validationService) {
        this.validationService = validationService;
    }

    @KafkaListener(topics = "passport-validation-request", groupId = "passport-validator-group")
    public void listen(String message) {
        // Пример сообщения: "series:number"
        String[] parts = message.split(":");
        if (parts.length == 2) {
            String series = parts[0];
            String number = parts[1];
            boolean isValid = validationService.validatePassport(series, number);
            // Отправляем результат обратно в Kafka
            validationService.sendValidationResult(series, number, isValid);
        }
    }
}
