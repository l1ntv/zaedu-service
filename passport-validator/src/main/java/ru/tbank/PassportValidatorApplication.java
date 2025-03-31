package ru.tbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PassportValidatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(PassportValidatorApplication.class, args);
    }
}
