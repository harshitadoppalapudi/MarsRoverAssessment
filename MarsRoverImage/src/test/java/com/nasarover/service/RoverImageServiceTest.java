package com.nasarover.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nasarover.config.AppConfig;
import com.nasarover.model.Camera;
import com.nasarover.model.Photo;
import com.nasarover.model.RoverResponse;
import com.nasarover.model.Rover;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the RoverImageService class.
 */
@ExtendWith(MockitoExtension.class)
public class RoverImageServiceTest {
    
    private RoverImageService roverImageService;
    
    @Mock
    private AppConfig mockConfig;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a partial mock of RoverImageService
        roverImageService = spy(new RoverImageService(mockConfig));
    }
    
    @Test
    public void testDownloadRoverImagesNoPhotos() throws Exception {
        // Mock the API response
        RoverResponse emptyResponse = new RoverResponse();
        emptyResponse.setPhotos(new ArrayList<>());
        
        // Configure the mock to return the empty response
        doReturn(emptyResponse).when(roverImageService).fetchRoverData(any(LocalDate.class));
        
        // Test with a sample date
        LocalDate testDate = LocalDate.of(2017, 2, 27);
        boolean result = roverImageService.downloadRoverImages(testDate);
        
        // Verify the result
        assertFalse(result, "Should return false when no photos are found");
        verify(roverImageService).fetchRoverData(testDate);
    }
    
    @Test
    public void testDownloadRoverImagesWithPhotos() throws Exception {
        // Create a test directory
        Path testDir = Files.createTempDirectory("nasa_rover_test");
        when(mockConfig.getOutputDirectory()).thenReturn(testDir.toString());
        
        // Create a mock response with photos
        RoverResponse mockResponse = createMockResponse();
        
        // Mock the API and download methods
        doReturn(mockResponse).when(roverImageService).fetchRoverData(any(LocalDate.class));
        doNothing().when(roverImageService).downloadImage(any(Photo.class), anyString());
        
        // Test with a sample date
        LocalDate testDate = LocalDate.of(2017, 2, 27);
        boolean result = roverImageService.downloadRoverImages(testDate);
        
        // Verify the result
        assertTrue(result, "Should return true when photos are found and processed");
        verify(roverImageService).fetchRoverData(testDate);
        verify(roverImageService, times(2)).downloadImage(any(Photo.class), anyString());
        
        // Clean up
        Files.walk(testDir)
            .sorted(java.util.Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }
    
    /**
     * Creates a mock RoverResponse with sample photos.
     */
    private RoverResponse createMockResponse() {
        RoverResponse response = new RoverResponse();
        List<Photo> photos = new ArrayList<>();
        
        // Create sample photos
        for (int i = 1; i <= 2; i++) {
            Photo photo = new Photo();
            photo.setId(i);
            photo.setImgSrc("https://example.com/photo" + i + ".jpg");
            photo.setEarthDate("2017-02-27");
            photo.setSol(1600 + i);
            
            Camera camera = new Camera();
            camera.setId(i);
            camera.setName("CAMERA" + i);
            camera.setFullName("Full Name Camera " + i);
            photo.setCamera(camera);
            
            Rover rover = new Rover();
            rover.setId(1);
            rover.setName("Curiosity");
            rover.setStatus("active");
            photo.setRover(rover);
            
            photos.add(photo);
        }
        
        response.setPhotos(photos);
        return response;
    }
}
