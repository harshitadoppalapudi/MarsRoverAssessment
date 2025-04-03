package com.nasarover.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for the application.
 */
public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    
    private final Properties properties;
    
    // Default values
    private static final String DEFAULT_API_BASE_URL = "https://api.nasa.gov";
    private static final String DEFAULT_API_KEY = "DEMO_KEY";
    private static final String DEFAULT_OUTPUT_DIRECTORY = "nasa_images";
    private static final String DEFAULT_DATES_FILE_PATH = "src/main/resources/dates.txt";
    
    public AppConfig() {
        properties = new Properties();
        loadProperties();
    }
    
    /**
     * Loads configuration properties from the application.properties file.
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                logger.info("Loaded configuration from application.properties");
            } else {
                logger.warn("application.properties not found, using default values");
            }
        } catch (IOException e) {
            logger.warn("Failed to load application.properties, using default values: {}", e.getMessage());
        }
    }
    
    /**
     * Gets the NASA API base URL.
     * 
     * @return the API base URL
     */
    public String getApiBaseUrl() {
        return properties.getProperty("nasa.api.base.url", DEFAULT_API_BASE_URL);
    }
    
    /**
     * Gets the NASA API key, first checking the environment variable, then the properties file.
     * 
     * @return the API key
     */
    public String getApiKey() {
        // First check environment variable
        String apiKey = System.getenv("NASA_API_KEY");
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            return apiKey;
        }
        
        // Then check properties file
        return properties.getProperty("nasa.api.key", DEFAULT_API_KEY);
    }
    
    /**
     * Gets the output directory for downloaded images.
     * 
     * @return the output directory path
     */
    public String getOutputDirectory() {
        return properties.getProperty("output.directory", DEFAULT_OUTPUT_DIRECTORY);
    }
    
    /**
     * Gets the path to the dates file.
     * 
     * @return the dates file path
     */
    public String getDatesFilePath() {
        return properties.getProperty("dates.file.path", DEFAULT_DATES_FILE_PATH);
    }
}
