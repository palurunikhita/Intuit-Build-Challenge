package com.dataanalysis;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

// Downloads CSV file from online source
public class CSVDownloader {

    public static String downloadCSV(String urlString, String localFilePath) throws IOException {
        File file = new File(localFilePath);

        // If file already exists, return the path
        if (file.exists()) {
            System.out.println("CSV file already exists at: " + localFilePath);
            return localFilePath;
        }

        System.out.println("Downloading CSV from: " + urlString);

        // Create directory if it doesn't exist
        Files.createDirectories(Paths.get(file.getParent()));

        // Download the file
        try (InputStream in = new URL(urlString).openStream();
             FileOutputStream out = new FileOutputStream(file)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("Downloaded successfully to: " + localFilePath);
        return localFilePath;
    }
}