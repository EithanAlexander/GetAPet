package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for setting up web-specific configurations.
 * This class implements WebMvcConfigurer to customize the default Spring MVC configuration.
 * It includes settings for CORS (Cross-Origin Resource Sharing) and serves resources from an external directory.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${external.images.path}")
    private String IMAGES_FOLDER_PATH;

    /**
     * Configures CORS mappings.
     * This method allows all origins, headers, and common HTTP methods for CORS requests,
     * enabling cross-origin requests to the application.
     *
     * @param registry the CorsRegistry to add mappings to
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply CORS to all routes
                .allowedOriginPatterns("*") // Allows all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allows all common methods
                .allowedHeaders("*"); // Allows all headers
    }

    /**
     * Configures resource handlers to serve static resources.
     * This method maps requests to the /images/** path to an external directory specified
     * by the IMAGES_FOLDER_PATH property, allowing images to be served from outside the application's jar.
     *
     * @param registry the ResourceHandlerRegistry to add resource handlers.
     */
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        String externalPath = "file:" + IMAGES_FOLDER_PATH;
        registry.addResourceHandler("/images/**")
                .addResourceLocations(externalPath);
    }
}
