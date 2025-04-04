package ru.tbank.zaedu.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.tbank.zaedu.repo.MasterProfileRepository;

@Service
public class PassportValidatorKafkaConsumer {

    private final MasterProfileRepository masterProfileRepository;

    public PassportValidatorKafkaConsumer(MasterProfileRepository masterProfileRepository) {
        this.masterProfileRepository = masterProfileRepository;
    }

    @KafkaListener(topics = "passport-validation-response", groupId = "zaedu-group")
    public void listen(String message) {
        String[] parts = message.split(":");
        if (parts.length == 3) {
            String series = parts[0];
            String number = parts[1];
            boolean isValid = "1".equals(parts[2]);

            masterProfileRepository
                    .findByPassportSeriesAndPassportNumber(series, number)
                    .ifPresent(master -> {
                        master.setIsConfirmedPassport(isValid);
                        masterProfileRepository.save(master);
                    });
        }
    }
}
