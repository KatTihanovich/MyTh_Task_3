package com.ferry.reader;

import com.ferry.entity.Car;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FerryReader {
    private static final Logger logger = LogManager.getLogger();

    public static List<Car> readCarsFromFile(String fileName) throws IOException {
        List<Car> cars = new ArrayList<>();
        URL filePath = FerryReader.class.getResource(fileName);
        if (filePath == null) {
            logger.error("Invalid file path: {}", fileName);
            throw new IllegalArgumentException("Invalid file path: " + fileName);
        }

        try (Stream<String> lines = Files.lines(Paths.get(filePath.toURI()))) {
            lines.forEach(line -> {
                try {
                    cars.add(parseCar(line));
                } catch (IllegalArgumentException e) {
                    logger.warn("Skipping malformed line: {}. Error: {}", line, e.getMessage());
                }
            });
        } catch (IOException | URISyntaxException e) {
            logger.error("Error reading cars from file: {}", fileName, e);
            throw new IOException("Failed to read cars from file: " + fileName, e);
        }

        return cars;
    }

    private static Car parseCar(String line) {
        String[] parts = line.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid format. Expected: type,weight");
        }

        String type = parts[0].trim();
        int weight;
        try {
            weight = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid weight: " + parts[1]);
        }

        return new Car(type, weight);
    }
}