package com.dataanalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

// Parsing Superstore CSV file
public class SalesDataParser {
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("M/d/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy")
    };

    public static List<SalesRecord> parseCSV(String filePath) throws IOException {
        List<SalesRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    System.out.println("Header: " + line);
                    continue;
                }

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    SalesRecord record = parseLine(line);
                    records.add(record);
                } catch (Exception e) {
                    System.err.println("Error parsing line " + lineNumber + ": " + line);
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }

        System.out.println("Successfully parsed " + records.size() + " records");
        return records;
    }

    private static SalesRecord parseLine(String line) {
        List<String> fields = parseCSVLine(line);

        if (fields.size() < 21) {
            throw new IllegalArgumentException("Invalid number of fields: " + fields.size());
        }

        String orderId = fields.get(1);
        LocalDate orderDate = parseDate(fields.get(2));
        String shipMode = fields.get(4);
        String segment = fields.get(7);
        String country = fields.get(8);
        String city = fields.get(9);
        String state = fields.get(10);
        String region = fields.get(12);
        String category = fields.get(14);
        String subCategory = fields.get(15);
        String productName = fields.get(16);
        double sales = parseDouble(fields.get(17));
        int quantity = parseInt(fields.get(18));
        double discount = parseDouble(fields.get(19));
        double profit = parseDouble(fields.get(20));

        return new SalesRecord(orderId, orderDate, shipMode, segment, country,
                city, state, region, category, subCategory,
                productName, sales, quantity, discount, profit);
    }

    // Parsing CSV line handling quoted fields
    private static List<String> parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        fields.add(currentField.toString().trim());
        return fields;
    }

    private static LocalDate parseDate(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }
        throw new IllegalArgumentException("Unable to parse date: " + dateStr);
    }

    private static double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}