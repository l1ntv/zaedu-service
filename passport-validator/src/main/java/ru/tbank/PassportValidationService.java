package ru.tbank;

import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class PassportValidationService {

    private static final Pattern PASSPORT_SERIES_PATTERN = Pattern.compile("^\\d{4}$");
    private static final Pattern PASSPORT_NUMBER_PATTERN = Pattern.compile("^\\d{6}$");

    private final KafkaProducer kafkaProducer;

    public PassportValidationService(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public boolean validatePassport(String series, String number) {
        return PASSPORT_SERIES_PATTERN.matcher(series).matches()
                && PASSPORT_NUMBER_PATTERN.matcher(number).matches();
    }

    public void sendValidationResult(String series, String number, boolean isValid) {
        kafkaProducer.sendValidationResult(series, number, isValid);
    }
}
