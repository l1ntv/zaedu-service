package ru.tbank.zaedu.liquebasemigrations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "spring.liquibase")
@Validated
public class LiquibaseCustomProperties {

    private String changeLog;
    private String insertChangeLog;

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public String getInsertChangeLog() {
        return insertChangeLog;
    }

    public void setInsertChangeLog(String insertChangeLog) {
        this.insertChangeLog = insertChangeLog;
    }
}