package ru.tbank.zaedu.s3storage.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.tbank.zaedu.s3storage.S3Client;

@Component
@RequiredArgsConstructor
public class S3Initializer implements CommandLineRunner {

    private final S3Client s3Client;

    private final S3Properties s3Properties;

    @SneakyThrows
    @Override
    public void run(String... args) {
        s3Client.createBucketIfNotExist(s3Properties.getBucket());
    }
}
