package ru.tbank.zaedu.liquebasemigrations;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import javax.sql.DataSource;
import java.sql.Connection;

public class InitialDataLoader {

    private final DataSource dataSource;

    public InitialDataLoader(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void loadInitialData(String insertChangelogPath) {
        try (Connection connection = dataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(insertChangelogPath, new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load initial data", e);
        }
    }
}
