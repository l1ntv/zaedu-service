package ru.tbank.zaedu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "ru.tbank.zaedu.repo")
@EntityScan(basePackages = "ru.tbank.zaedu.models")
public class ZaeduApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZaeduApplication.class, args);
	}

}
