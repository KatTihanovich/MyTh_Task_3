package com.ferry.manager;

import com.ferry.entity.Ferry;
import com.ferry.service.CarGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FerryManager {
    private static final Logger logger = LogManager.getLogger(FerryManager.class);

    private static volatile FerryManager instance;

    private final Ferry ferry;
    private final ExecutorService executor;

    private FerryManager() {
        this.ferry = new Ferry(10, 5000);
        this.executor = Executors.newFixedThreadPool(1);
    }

    public static FerryManager getInstance() {
        if (instance == null) {
            synchronized (FerryManager.class) {
                if (instance == null) {
                    instance = new FerryManager();
                }
            }
        }
        return instance;
    }

    public void startSimulation(String filePath) {
        logger.info("Starting ferry simulation...");
        executor.submit(new CarGenerator(ferry, filePath));
        simulateFerryOperations();
    }

    private void simulateFerryOperations() {
        new Thread(() -> {
            while (!executor.isShutdown()) {
                logger.info("Ferry is preparing to cross.");
                ferry.startCrossing();

                try {
                    TimeUnit.SECONDS.sleep(5); // Simulate crossing time
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Ferry operation interrupted: ", e);
                }

                logger.info("Ferry is unloading cars.");
                ferry.unloadCars();
            }
        }).start();
    }

    public void stopSimulation() {
        logger.info("Stopping ferry simulation...");
        executor.shutdown();
    }

    public Ferry getFerry() {
        return ferry;
    }
}