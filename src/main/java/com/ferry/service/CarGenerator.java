package com.ferry.service;

import com.ferry.entity.Ferry;
import com.ferry.entity.Car;
import com.ferry.reader.FerryReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CarGenerator implements Runnable {
    private static final Logger logger = LogManager.getLogger(CarGenerator.class);
    private final Ferry ferry;
    private final List<Car> cars;

    public CarGenerator(Ferry ferry, String filePath) {
        this.ferry = ferry;
        try {
            this.cars = FerryReader.readCarsFromFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read car data from file", e);
        }
    }

    @Override
    public void run() {
        for (Car car : cars) {
            if (Thread.currentThread().isInterrupted()) {
                logger.warn("Car generation interrupted.");
                break;
            }


            logger.info("Current ferry queue before loading: {}", ferry.getCarsOnFerry());
            ferry.loadCar(car);

            logger.info("Current ferry queue after loading: {}", ferry.getCarsOnFerry());
            logger.info("Current waiting queue: {}", ferry.getWaitingCars());

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Car generation interrupted during sleep.", e);
                break;
            }
        }
    }
}