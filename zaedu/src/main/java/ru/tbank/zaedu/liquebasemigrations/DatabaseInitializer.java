package ru.tbank.zaedu.liquebasemigrations;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


import javax.sql.DataSource;

@Configuration
public class DatabaseInitializer {

    private final DataSource dataSource;
    private final LiquibaseCustomProperties liquibaseCustomProperties;

    @Autowired
    public DatabaseInitializer(DataSource dataSource, LiquibaseCustomProperties liquibaseCustomProperties) {
        this.dataSource = dataSource;
        this.liquibaseCustomProperties = liquibaseCustomProperties;
    }

    @PostConstruct
    public void initializeDatabase() {
        // Выполняем миграции
        LiquibaseMigrationRunner migrationRunner = new LiquibaseMigrationRunner(dataSource);
        migrationRunner.runMigrations(liquibaseCustomProperties.getChangeLog());

        // Заполняем начальные данные (если нужно)
        if (shouldLoadInitialData()) {
            InitialDataLoader dataLoader = new InitialDataLoader(dataSource);
            dataLoader.loadInitialData(liquibaseCustomProperties.getInsertChangeLog());
        }
    }

    private boolean shouldLoadInitialData() {
        // Здесь можно добавить логику для определения, нужно ли заполнять начальные данные
        // Например, проверка профиля среды (dev, test, prod)
        return !"test".equals(System.getProperty("spring.profiles.active"));
    }
}
