package com.nasarover.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a camera on the Mars Rover.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Camera {
    
    private long id;
    private String name;
    
    @JsonProperty("rover_id")
    private long roverId;
    
    @JsonProperty("full_name")
    private String fullName;
    
    public Camera() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRoverId() {
        return roverId;
    }

    public void setRoverId(long roverId) {
        this.roverId = roverId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Camera{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", roverId=" + roverId +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
