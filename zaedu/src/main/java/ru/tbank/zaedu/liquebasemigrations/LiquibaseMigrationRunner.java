package ru.tbank.zaedu.liquebasemigrations;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import javax.sql.DataSource;
import java.sql.Connection;

public class LiquibaseMigrationRunner {

    private final DataSource dataSource;

    public LiquibaseMigrationRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void runMigrations(String changelogPath) {
        try (Connection connection = dataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(changelogPath, new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts());
        } catch (Exception e) {
            throw new RuntimeException("Failed to run Liquibase migrations", e);
        }
    }
}
