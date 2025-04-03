package com.nasarover.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nasarover.exception.DateParsingException;

/**
 * Unit tests for the DateParser class.
 */
public class DateParserTest {
    
    private DateParser dateParser;
    
    @BeforeEach
    public void setUp() {
        dateParser = new DateParser();
    }
    // 02/27/2017
    @Test
    public void testParseMMddyy() throws DateParsingException {
        LocalDate date = dateParser.parseDate("02/27/17");
        assertEquals(LocalDate.of(2017, 2, 27), date);
    }
    // June 2, 2018
    @Test
    public void testParseMonthDayYear() throws DateParsingException {
        LocalDate date = dateParser.parseDate("June 2, 2018");
        assertEquals(LocalDate.of(2018, 6, 2), date);
    }
    // Jul-13-2016
    @Test
    public void testParseMonthDashDayDashYear() throws DateParsingException {
        LocalDate date = dateParser.parseDate("Jul-13-2016");
        assertEquals(LocalDate.of(2016, 7, 13), date);
    }
    // April 31, 2018
    @Test
    public void testParseInvalidDate() {
        // Test parsing April 31, 2018 (April only has 30 days)
        DateParsingException exception = assertThrows(DateParsingException.class, () -> {
            dateParser.parseDate("April 31, 2018");
        });
        
        // Verify the error message
        assertTrue(exception.getMessage().contains("April has only 30 days"));
    }
    
    @Test
    public void testParseInvalidDateFormat() {
        assertThrows(DateParsingException.class, () -> {
            dateParser.parseDate("Invalid Date Format");
        });
    }
    
    @Test
    public void testParseNullDate() {
        assertThrows(DateParsingException.class, () -> {
            dateParser.parseDate(null);
        });
    }
    
    @Test
    public void testParseEmptyDate() {
        assertThrows(DateParsingException.class, () -> {
            dateParser.parseDate("");
        });
    }
    
    @ParameterizedTest
    @CsvSource({
        "2/3/2020, 2020-02-03",
        "12/25/2019, 2019-12-25",
        "June 15 2021, 2021-06-15",
        "Mar-5-2022, 2022-03-05"
    })
    public void testParseVariousDateFormats(String input, String expected) throws DateParsingException {
        LocalDate date = dateParser.parseDate(input);
        assertEquals(LocalDate.parse(expected), date);
    }
    
    @Test
    public void testParseInvalidMonthDay() {
        // Test parsing June 31, 2021 (June only has 30 days)
        DateParsingException exception = assertThrows(DateParsingException.class, () -> {
            dateParser.parseDate("June 31, 2021");
        });
        
        // Verify the error message
        assertTrue(exception.getMessage().contains("June has only 30 days"));
    }
    
    @Test
    public void testParseFutureDate() {
        // Test parsing a future date
        LocalDate futureDate = LocalDate.now().plusYears(1);
        String futureDateStr = futureDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        
        DateParsingException exception = assertThrows(DateParsingException.class, () -> {
            dateParser.parseDate(futureDateStr);
        });
        
        // Verify the error message
        assertTrue(exception.getMessage().contains("Date cannot be in the future"));
    }
    
    @Test
    public void testParseBeforeLandingDate() {
        // Test parsing a date before Curiosity's landing
        DateParsingException exception = assertThrows(DateParsingException.class, () -> {
            dateParser.parseDate("July 1, 2012");
        });
        
        // Verify the error message
        assertTrue(exception.getMessage().contains("Date must be after Curiosity's landing date"));
    }
}

