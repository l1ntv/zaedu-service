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

    public void sendValidationResult(String series, String number, boolean isValid) {
        String message = series + ":" + number + ":" + (isValid ? "1" : "0");
        kafkaTemplate.send("passport-validation-response", message);
    }
}
