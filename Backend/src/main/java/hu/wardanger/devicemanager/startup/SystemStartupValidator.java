package hu.wardanger.devicemanager.startup;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class SystemStartupValidator {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public SystemStartupValidator(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isDatabaseConnectionValid() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLiquibaseReady() {
        try {
            Integer changeSetCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM DATABASECHANGELOG",
                    Integer.class
            );

            return changeSetCount != null && changeSetCount > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void validateStartupState() {
        boolean databaseOk = isDatabaseConnectionValid();
        boolean liquibaseOk = isLiquibaseReady();

        if (!databaseOk) {
            throw new IllegalStateException("Database connection is not available.");
        }

        if (!liquibaseOk) {
            throw new IllegalStateException("Liquibase changelog table is missing or no changesets were executed.");
        }
    }
}