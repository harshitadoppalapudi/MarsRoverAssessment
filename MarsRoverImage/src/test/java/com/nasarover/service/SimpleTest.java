package com.nasarover.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Simple test class for date parsing functionality.
 * This class demonstrates basic date parsing and validation without external dependencies.
 */
public class SimpleTest {
    
    /**
     * Main method to run the date parsing tests.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Test 1: Basic date parsing (MM/dd/yy format)
        testBasicDateParsing();
        
        // Test 2: Invalid date validation
        testInvalidDate();
    }
    
    /**
     * Tests parsing of a basic date in MM/dd/yy format.
     */
    private static void testBasicDateParsing() {
        try {
            String dateStr = "02/27/17";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
            LocalDate date = LocalDate.parse(dateStr, formatter);
            
            System.out.println("Test passed: Successfully parsed date " + dateStr + " to " + date);
        } catch (Exception e) {
            System.out.println("Test failed: Could not parse date - " + e.getMessage());
        }
    }
    
    /**
     * Tests validation of an invalid date (April 31, 2018).
     */
    private static void testInvalidDate() {
        try {
            String dateStr = "April 31, 2018";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            LocalDate date = LocalDate.parse(dateStr, formatter);
            
            System.out.println("Test failed: Should not have parsed invalid date " + dateStr);
        } catch (Exception e) {
            System.out.println("Test passed: Correctly rejected invalid date - " + e.getMessage());
        }
    }
} 