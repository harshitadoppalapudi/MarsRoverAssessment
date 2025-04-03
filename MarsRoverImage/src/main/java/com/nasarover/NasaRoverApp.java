package com.nasarover;

import com.nasarover.config.AppConfig;
import com.nasarover.exception.DateParsingException;
import com.nasarover.service.DateParser;
import com.nasarover.service.RoverImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application class for the NASA Mars Rover Image Downloader.
 * This application reads dates from a text file, queries the NASA Mars Rover API,
 * and downloads images taken on those dates.
 */
public class NasaRoverApp {
    private static final Logger logger = LoggerFactory.getLogger(NasaRoverApp.class);

    public static void main(String[] args) {
        logger.info("Starting NASA Mars Rover Image Downloader");
        
        AppConfig config = new AppConfig();
        DateParser dateParser = new DateParser();
        RoverImageService roverService = new RoverImageService(config);
        
        // Create output directory if it doesn't exist
        Path outputDir = Paths.get(config.getOutputDirectory());
        try {
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
                logger.info("Created output directory: {}", outputDir);
            }
        } catch (IOException e) {
            logger.error("Failed to create output directory: {}", e.getMessage());
            System.exit(1);
        }
        
        // Read dates from file
        List<String> dateStrings = readDatesFromFile(config.getDatesFilePath());
        if (dateStrings.isEmpty()) {
            logger.error("No dates found in the dates file. Exiting.");
            System.exit(1);
        }
        
        // Process each date
        int successCount = 0;
        int invalidDateCount = 0;
        int noImagesCount = 0;
        
        for (String dateString : dateStrings) {
            logger.info("Processing date: {}", dateString);
            try {
                LocalDate date = dateParser.parseDate(dateString);
                boolean success = roverService.downloadRoverImages(date);
                if (success) {
                    successCount++;
                    logger.info("Successfully downloaded images for date: {}", dateString);
                } else {
                    noImagesCount++;
                    logger.info("No images found for date: {}", dateString);
                }
            } catch (DateParsingException e) {
                invalidDateCount++;
                // Print a user-friendly error message
                System.out.println("ERROR: " + e.getMessage());
                logger.error("Invalid date {}: {}", dateString, e.getMessage());
            } catch (Exception e) {
                logger.error("Error processing date {}: {}", dateString, e.getMessage());
                System.out.println("ERROR: Failed to process date " + dateString + ": " + e.getMessage());
            }
        }
        
        // Print summary
        logger.info("Image download complete. Summary:");
        logger.info("- Successfully processed dates: {}/{}", successCount, dateStrings.size());
        logger.info("- Invalid dates: {}", invalidDateCount);
        logger.info("- Dates with no images: {}", noImagesCount);
        
        // Print a user-friendly summary
        System.out.println("\n=== NASA Mars Rover Image Download Summary ===");
        System.out.println("Total dates processed: " + dateStrings.size());
        System.out.println("Successfully downloaded images: " + successCount);
        System.out.println("Invalid dates: " + invalidDateCount);
        System.out.println("Dates with no images: " + noImagesCount);
        System.out.println("=============================================");
    }
    
    /**
     * Reads dates from the specified file.
     * 
     * @param filePath path to the dates file
     * @return list of date strings
     */
    private static List<String> readDatesFromFile(String filePath) {
        List<String> dates = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    dates.add(line);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading dates file: {}", e.getMessage());
        }
        return dates;
    }
}
