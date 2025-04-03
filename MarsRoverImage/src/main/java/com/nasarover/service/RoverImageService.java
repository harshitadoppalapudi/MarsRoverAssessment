package com.nasarover.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nasarover.config.AppConfig;
import com.nasarover.exception.ApiRequestException;
import com.nasarover.exception.ImageDownloadException;
import com.nasarover.model.Photo;
import com.nasarover.model.RoverResponse;
import com.nasarover.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for interacting with the NASA Mars Rover API and downloading images.
 */
public class RoverImageService {
    private static final Logger logger = LoggerFactory.getLogger(RoverImageService.class);
    
    private final AppConfig config;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    
    public RoverImageService(AppConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Downloads Mars Rover images for a specific date.
     *
     * @param date the date to retrieve images for
     * @return true if at least one image was successfully downloaded, false otherwise
     */
    public boolean downloadRoverImages(LocalDate date) {
        try {
            RoverResponse response = fetchRoverData(date);
            List<Photo> photos = response.getPhotos();
            
            if (photos == null || photos.isEmpty()) {
                logger.warn("No photos found for date: {}", date);
                return false;
            }
            
            logger.info("Found {} photos for date: {}", photos.size(), date);
            
            // Create directory for this date
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            File dateDir = new File(config.getOutputDirectory() + File.separator + dateStr);
            if (!dateDir.exists() && !dateDir.mkdirs()) {
                throw new IOException("Failed to create directory for date: " + dateStr);
            }
            
            // Download each photo
            int downloadCount = 0;
            for (Photo photo : photos) {
                try {
                    downloadImage(photo, dateDir.getPath());
                    downloadCount++;
                } catch (ImageDownloadException e) {
                    logger.error("Failed to download image {}: {}", photo.getId(), e.getMessage());
                }
            }
            
            logger.info("Successfully downloaded {}/{} images for date: {}", 
                    downloadCount, photos.size(), date);
            return downloadCount > 0;
        } catch (ApiRequestException e) {
            logger.error("API request failed for date {}: {}", date, e.getMessage());
            return false;
        } catch (IOException e) {
            logger.error("I/O error for date {}: {}", date, e.getMessage());
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted while processing date {}", date);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error for date {}: {}", date, e.getMessage());
            return false;
        }
    }

    /**
     * Fetches Mars Rover data from NASA API for a specific date.
     *
     * @param date the date to fetch data for
     * @return RoverResponse containing the API response
     * @throws ApiRequestException if the API request fails
     * @throws IOException if there is an I/O error
     * @throws InterruptedException if the thread is interrupted
     */
    RoverResponse fetchRoverData(LocalDate date) 
            throws ApiRequestException, IOException, InterruptedException {
        String dateParam = date.format(DateTimeFormatter.ISO_DATE);
        String apiUrl = String.format("%s/mars-photos/api/v1/rovers/curiosity/photos?earth_date=%s&api_key=%s",
                config.getApiBaseUrl(),
                URLEncoder.encode(dateParam, StandardCharsets.UTF_8),
                config.getApiKey());
        
        logger.debug("Requesting Mars Rover data for date: {}", dateParam);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new ApiRequestException("NASA API request failed with status code: " + 
                    response.statusCode() + ", body: " + response.body());
        }
        
        return objectMapper.readValue(response.body(), RoverResponse.class);
    }

    /**
     * Downloads an image from a URL to a local file.
     *
     * @param photo the photo object containing image metadata
     * @param outputDir the directory to save the image to
     * @throws ImageDownloadException if the image download fails
     */
    void downloadImage(Photo photo, String outputDir) throws ImageDownloadException {
        String imageUrl = photo.getImgSrc();
        String fileName = getFileNameFromUrl(imageUrl);
        String outputPath = outputDir + File.separator + fileName;
        
        logger.debug("Downloading image: {} to {}", imageUrl, outputPath);
        
        try {
            byte[] imageData = HttpUtil.downloadFile(imageUrl);
            
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                fos.write(imageData);
            }
            
            logger.debug("Successfully downloaded image: {}", fileName);
        } catch (IOException e) {
            throw new ImageDownloadException("Failed to download image: " + imageUrl, e);
        }
    }

    /**
     * Extracts a filename from a URL.
     *
     * @param url the URL to extract the filename from
     * @return the filename
     */
    private String getFileNameFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
}
