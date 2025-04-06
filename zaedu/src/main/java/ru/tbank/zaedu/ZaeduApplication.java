package ru.tbank.zaedu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.tbank.zaedu.liquebasemigrations.LiquibaseCustomProperties;

@SpringBootApplication
@EnableConfigurationProperties(LiquibaseCustomProperties.class)
public class ZaeduApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZaeduApplication.class, args);
    }
}
