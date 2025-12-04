package com.dataanalysis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SalesDataParser
 */
class SalesDataParserTest {

    @Test
    @DisplayName("Test CSV parsing with valid data")
    void testParseValidCSV(@TempDir Path tempDir) throws IOException {
        // Create temporary CSV file
        Path csvFile = tempDir.resolve("test.csv");

        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("Row ID,Order ID,Order Date,Ship Date,Ship Mode,Customer ID,Customer Name,Segment,Country,City,State,Postal Code,Region,Product ID,Category,Sub-Category,Product Name,Sales,Quantity,Discount,Profit\n");
            writer.write("1,ORDER-001,1/3/2024,1/5/2024,Standard Class,CID-001,John Doe,Consumer,United States,New York,NY,10001,East,PROD-001,Technology,Phones,iPhone,1200.00,2,0.1,240.00\n");
            writer.write("2,ORDER-002,2/15/2024,2/17/2024,Second Class,CID-002,Jane Smith,Corporate,United States,Los Angeles,CA,90001,West,PROD-002,Furniture,Chairs,Office Chair,450.00,3,0.0,135.00\n");
        }

        List<SalesRecord> records = SalesDataParser.parseCSV(csvFile.toString());

        assertEquals(2, records.size());

        SalesRecord first = records.get(0);
        assertEquals("ORDER-001", first.getOrderId());
        assertEquals(LocalDate.of(2024, 1, 3), first.getOrderDate());
        assertEquals("Technology", first.getCategory());
        assertEquals(1200.00, first.getSales(), 0.01);
        assertEquals(2, first.getQuantity());
    }

    @Test
    @DisplayName("Test CSV parsing with quoted fields")
    void testParseCSVWithQuotes(@TempDir Path tempDir) throws IOException {
        Path csvFile = tempDir.resolve("test_quotes.csv");

        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("Row ID,Order ID,Order Date,Ship Date,Ship Mode,Customer ID,Customer Name,Segment,Country,City,State,Postal Code,Region,Product ID,Category,Sub-Category,Product Name,Sales,Quantity,Discount,Profit\n");
            writer.write("1,ORDER-001,1/3/2024,1/5/2024,Standard Class,CID-001,\"Doe, John\",Consumer,United States,New York,NY,10001,East,PROD-001,Technology,Phones,\"Apple iPhone 13 Pro Max, 256GB\",1200.00,2,0.1,240.00\n");
        }

        List<SalesRecord> records = SalesDataParser.parseCSV(csvFile.toString());

        assertEquals(1, records.size());
        assertTrue(records.get(0).getProductName().contains("256GB"));
    }

    @Test
    @DisplayName("Test CSV parsing with empty file")
    void testParseEmptyCSV(@TempDir Path tempDir) throws IOException {
        Path csvFile = tempDir.resolve("empty.csv");

        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("Row ID,Order ID,Order Date,Ship Date,Ship Mode,Customer ID,Customer Name,Segment,Country,City,State,Postal Code,Region,Product ID,Category,Sub-Category,Product Name,Sales,Quantity,Discount,Profit\n");
        }

        List<SalesRecord> records = SalesDataParser.parseCSV(csvFile.toString());

        assertTrue(records.isEmpty());
    }

    @Test
    @DisplayName("Test file not found exception")
    void testFileNotFound() {
        assertThrows(IOException.class, () -> {
            SalesDataParser.parseCSV("nonexistent.csv");
        });
    }

    @Test
    @DisplayName("Test parsing handles malformed lines gracefully")
    void testParseMalformedLines(@TempDir Path tempDir) throws IOException {
        Path csvFile = tempDir.resolve("malformed.csv");

        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("Row ID,Order ID,Order Date,Ship Date,Ship Mode,Customer ID,Customer Name,Segment,Country,City,State,Postal Code,Region,Product ID,Category,Sub-Category,Product Name,Sales,Quantity,Discount,Profit\n");
            writer.write("1,ORDER-001,invalid-date,1/5/2024,Standard Class,CID-001,John Doe,Consumer,United States,New York,NY,10001,East,PROD-001,Technology,Phones,iPhone,1200.00,2,0.1,240.00\n");
            writer.write("2,ORDER-002,2/15/2024,2/17/2024,Second Class,CID-002,Jane Smith,Corporate,United States,Los Angeles,CA,90001,West,PROD-002,Furniture,Chairs,Office Chair,450.00,3,0.0,135.00\n");
        }

        List<SalesRecord> records = SalesDataParser.parseCSV(csvFile.toString());

        // Should successfully parse the valid line
        assertEquals(1, records.size());
        assertEquals("ORDER-002", records.get(0).getOrderId());
    }
}