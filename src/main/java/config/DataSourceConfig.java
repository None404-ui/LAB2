package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
public class DataSourceConfig {

    private final Environment environment;

    public DataSourceConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource() {
        boolean isTest = Arrays.asList(environment.getActiveProfiles()).contains("test");

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        if (isTest) {
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
            dataSource.setUsername("sa");
            dataSource.setPassword("");
        } else {
            String driver = environment.getProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
            String url = environment.getProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/laba");
            String username = environment.getProperty("spring.datasource.username", "postgres");
            String password = environment.getProperty("spring.datasource.password", "admin");

            dataSource.setDriverClassName(driver);
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
        }

        return dataSource;
    }
}
