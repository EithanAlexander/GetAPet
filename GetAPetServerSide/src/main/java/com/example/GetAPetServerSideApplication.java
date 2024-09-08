package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

/**
 * Main entry point for the GetAPetServerSideApplication.
 * This class bootstraps the Spring Boot application and logs the startup status.
 * Utilizes SLF4J for logging.
 */
@SpringBootApplication
@Slf4j
public class GetAPetServerSideApplication {

    /**
     * The main method to start the Spring Boot application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(GetAPetServerSideApplication.class, args);
        log.info("Application GetAPetServerSideApplication is up and running");
    }

}
