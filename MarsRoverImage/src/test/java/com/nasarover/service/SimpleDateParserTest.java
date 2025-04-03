package com.nasarover.service;

import java.time.LocalDate;

public class SimpleDateParserTest {
    
    private DateParser dateParser;
    
    public void setUp() {
        dateParser = new DateParser();
    }
    
    public void testParseMMddyy() {
        try {
            setUp();
            LocalDate date = dateParser.parseDate("02/27/17");
            if (!date.equals(LocalDate.of(2017, 2, 27))) {
                System.out.println("Test failed: Expected 2017-02-27 but got " + date);
            } else {
                System.out.println("Test passed: parseMMddyy");
            }
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
        }
    }
    
    public void testParseInvalidDate() {
        try {
            setUp();
            dateParser.parseDate("April 31, 2018");
            System.out.println("Test failed: Should have thrown exception for invalid date");
        } catch (Exception e) {
            if (e.getMessage().contains("April has only 30 days")) {
                System.out.println("Test passed: parseInvalidDate");
            } else {
                System.out.println("Test failed: Wrong error message");
            }
        }
    }
    
    public static void main(String[] args) {
        SimpleDateParserTest test = new SimpleDateParserTest();
        test.testParseMMddyy();
        test.testParseInvalidDate();
    }
} 