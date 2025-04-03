package com.nasarover.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Represents the response from the NASA Mars Rover API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoverResponse {
    
    private List<Photo> photos;
    
    public RoverResponse() {
    }
    
    public List<Photo> getPhotos() {
        return photos;
    }
    
    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
    
    @Override
    public String toString() {
        return "RoverResponse{" +
                "photos=" + (photos != null ? photos.size() : "null") +
                '}';
    }
}
