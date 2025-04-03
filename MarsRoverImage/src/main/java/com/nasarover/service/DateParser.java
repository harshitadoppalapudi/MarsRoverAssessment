package com.nasarover.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nasarover.exception.DateParsingException;

/**
 * Handles parsing of date strings in various formats.
 */
public class DateParser {
    private static final Logger logger = LoggerFactory.getLogger(DateParser.class);
    
    // Supported date formats
    private final List<DateTimeFormatter> formatters;
    
    public DateParser() {
        formatters = new ArrayList<>();
        // MM/dd/yy - e.g., 02/27/17
        formatters.add(DateTimeFormatter.ofPattern("MM/dd/yy", Locale.US));
        // MMMM d, yyyy - e.g., June 2, 2018
        formatters.add(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US));
        // MMM-dd-yyyy - e.g., Jul-13-2016
        formatters.add(DateTimeFormatter.ofPattern("MMM-dd-yyyy", Locale.US));
        // MMMM dd, yyyy - e.g., April 31, 2018
        formatters.add(DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.US));
        
        // Additional formats for robustness
        formatters.add(DateTimeFormatter.ofPattern("M/d/yyyy", Locale.US));
        formatters.add(DateTimeFormatter.ofPattern("M/d/yy", Locale.US));
        formatters.add(DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.US));
        formatters.add(DateTimeFormatter.ofPattern("MMM-d-yyyy", Locale.US));
    }
    
    /**
     * Parses a date string in various formats.
     * 
     * @param dateStr the date string to parse
     * @return a LocalDate object representing the parsed date
     * @throws DateParsingException if the date cannot be parsed
     */
    public LocalDate parseDate(String dateStr) throws DateParsingException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new DateParsingException("Date string cannot be null or empty");
        }
        
        // Try parsing with original string first
        LocalDate date = tryParse(dateStr);
        if (date != null) {
            return validateDate(date, dateStr);
        }
        
        // If original parsing fails, try with normalized string
        String normalizedDateStr = normalizeDate(dateStr);
        date = tryParse(normalizedDateStr);
        if (date != null) {
            return validateDate(date, dateStr);
        }
        
        throw new DateParsingException("Could not parse date: " + dateStr + 
                ". Supported formats include MM/dd/yy, MMMM d, yyyy, and MMM-dd-yyyy.");
    }
    
    private LocalDate tryParse(String dateStr) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                logger.debug("Successfully parsed date '{}' using format '{}'", 
                        dateStr, formatter.toString());
                return date;
            } catch (DateTimeParseException e) {
                // Continue to next formatter
            }
        }
        return null;
    }
    
    /**
     * Normalizes a date string to handle special cases.
     * 
     * @param dateStr the date string to normalize
     * @return normalized date string
     */
    private String normalizeDate(String dateStr) {
        String normalized = dateStr.trim();
        
        // Handle case where year is missing (e.g., "June 15" -> "June 15, 2021")
        if (normalized.matches("^[A-Za-z]+ \\d{1,2}$")) {
            normalized = normalized + ", 2021";
        }
        // Add comma before year if missing (e.g., "June 15 2021" -> "June 15, 2021")
        else if (normalized.matches("^[A-Za-z]+ \\d{1,2} \\d{4}$")) {
            String[] parts = normalized.split(" ");
            normalized = parts[0] + " " + parts[1] + ", " + parts[2];
        }
        
        return normalized;
    }
    
    /**
     * Validates the parsed date for logical correctness.
     * 
     * @param date the parsed date
     * @param originalDateStr the original date string
     * @return the validated date
     * @throws DateParsingException if the date is invalid
     */
    private LocalDate validateDate(LocalDate date, String originalDateStr) throws DateParsingException {
        try {
            // Check if the day is valid for the month
            int maxDays = date.getMonth().length(date.isLeapYear());
            int day = date.getDayOfMonth();
            
            // For dates in MM/dd/yy format, we don't need to extract the day
            if (!originalDateStr.contains("/") && originalDateStr.contains(",")) {
                // For dates like "June 31, 2021", extract the day from the string
                String[] parts = originalDateStr.split("[,\\s]+");
                day = Integer.parseInt(parts[1].trim());
            }
            
            if (day > maxDays) {
                String monthName = date.getMonth().toString().toLowerCase();
                monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
                String errorMsg = "Invalid date: " + originalDateStr + ". " + monthName + " has only " + maxDays + " days.";
                logger.warn(errorMsg);
                throw new DateParsingException(errorMsg);
            }
            
            // Check if date is in the future
            if (date.isAfter(LocalDate.now())) {
                String errorMsg = "Invalid date: " + originalDateStr + ". Date cannot be in the future.";
                logger.warn(errorMsg);
                throw new DateParsingException(errorMsg);
            }
            
            // Check if date is too old (before 2012 when Curiosity landed)
            if (date.isBefore(LocalDate.of(2012, 8, 6))) {
                String errorMsg = "Invalid date: " + originalDateStr + ". Date must be after Curiosity's landing date (August 6, 2012).";
                logger.warn(errorMsg);
                throw new DateParsingException(errorMsg);
            }
            
            return date;
        } catch (DateTimeException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            String errorMsg = "Invalid date format: " + originalDateStr + ". Please use one of the supported formats (MM/dd/yy, MMMM d, yyyy, or MMM-dd-yyyy).";
            logger.warn(errorMsg);
            throw new DateParsingException(errorMsg);
        }
    }
}
