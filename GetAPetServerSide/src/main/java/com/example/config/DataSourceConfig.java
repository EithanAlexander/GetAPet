package com.example.config;

import lombok.Getter;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up the DataSource using Apache Commons DBCP2.
 * This class reads the database configuration properties from the application properties file
 * and initializes a BasicDataSource with these properties.
 *
 * The configured DataSource is used for JDBC operations, providing a connection pool
 * for efficient database access.
 */
@Getter
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.maxTotal}")
    private int maxTotal;

    /**
     * Creates and configures a BasicDataSource bean.
     * The BasicDataSource is used by the application to obtain JDBC connections.
     * It manages the connection pool, which improves the efficiency of JDBC operations
     * by reusing existing connections instead of creating a new connection for each request.
     *
     * @return a configured BasicDataSource
     */
    @Bean
    public BasicDataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxTotal(maxTotal);
        return dataSource;
    }
}
