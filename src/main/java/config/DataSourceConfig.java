package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        String activeProfile = System.getProperty("spring.profiles.active");
        boolean isTest = "test".equals(activeProfile);
        
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        if (isTest) {
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
            dataSource.setUsername("sa");
            dataSource.setPassword("");
        } else {
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl("jdbc:postgresql://localhost:5432/laba");
            dataSource.setUsername("postgres");
            dataSource.setPassword("admin");
        }
        
        return dataSource;
    }
}

