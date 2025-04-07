package ru.tbank.zaedu;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tbank.zaedu.liquebasemigrations.LiquibaseMigrationRunner;

import javax.sql.DataSource;

@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("zaEdu")
            .withUsername("root")
            .withPassword("root");

    @Container
    private static final KafkaContainer KAFKA = new KafkaContainer("7.4.0"); // Версия Kafka

    @Container
    private static final GenericContainer<?> MINIO = new GenericContainer<>("minio/minio:latest")
            .withCommand("server /data") // Запуск MinIO сервера
            .withEnv("MINIO_ROOT_USER", "root") // Установка логина
            .withEnv("MINIO_ROOT_PASSWORD", "minio123") // Установка пароля
            .withExposedPorts(9000); // Порт MinIO

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);

        registry.add("s3.url", () -> "http://" + MINIO.getHost() + ":" + MINIO.getMappedPort(9000));
        registry.add("s3.accessKey", () -> "root");
        registry.add("s3.secretKey", () -> "minio123");
        registry.add("s3.bucket", () -> "zaedu");
    }

    @BeforeAll
    static void setUp() {
        String jdbcUrl = POSTGRES.getJdbcUrl();
        String username = POSTGRES.getUsername();
        String password = POSTGRES.getPassword();
        String changelogPath = "db/changelog/main-changelog.xml"; // Путь к файлу миграций

        // Запускаем миграции
        LiquibaseMigrationRunner migrationRunner = new LiquibaseMigrationRunner(createDataSource(jdbcUrl, username, password));
        migrationRunner.runMigrations(changelogPath);

        // Создаем бакет в MinIO
        try {
            MINIO.execInContainer(
                    "mc",
                    "alias",
                    "set",
                    "local",
                    "http://" + MINIO.getHost() + ":" + MINIO.getMappedPort(9000),
                    "root",
                    "minio123"
            );
            MINIO.execInContainer("mc", "mb", "local/zaedu");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create MinIO bucket", e);
        }
    }

    private static javax.sql.DataSource createDataSource(String jdbcUrl, String username, String password) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}