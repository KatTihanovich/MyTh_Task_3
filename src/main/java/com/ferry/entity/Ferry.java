package com.ferry.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Ferry {
    private static final Logger logger = LogManager.getLogger(Ferry.class);

    private final int maxCapacity;
    private final int maxWeight;
    private final Queue<Car> carsOnFerry = new LinkedList<>();
    private final Queue<Car> waitingCars = new LinkedList<>();
    private int currentWeight = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private boolean isCrossing = false;

    public Ferry(int maxCapacity, int maxWeight) {
        this.maxCapacity = maxCapacity;
        this.maxWeight = maxWeight;
    }

    public void loadCar(Car car) {
        lock.lock();
        try {
            if (isCrossing) {
                logger.info("Cannot load car: {}. Ferry is crossing the river.", car);
                return;
            }

            if (carsOnFerry.size() < maxCapacity && (currentWeight + car.getWeight()) <= maxWeight) {
                carsOnFerry.add(car);
                currentWeight += car.getWeight();
                logger.info("Car loaded: {}", car);
            } else {
                waitingCars.add(car);
                logger.info("Cannot load car: {}. Ferry is full or overweight. Car added to the queue.", car);
            }
        } finally {
            lock.unlock();
        }
    }

    public void unloadCars() {
        lock.lock();
        try {
            if (isCrossing) {
                logger.info("Cannot unload cars while crossing the river.");
                return;
            }

            if (carsOnFerry.isEmpty()) {
                logger.info("No cars to unload. Ferry is empty.");
            } else {
                carsOnFerry.clear();
                currentWeight = 0;
                logger.info("All cars unloaded. Ferry is now empty.");

                loadCarsFromQueue();
            }
        } finally {
            lock.unlock();
        }
    }

    private void loadCarsFromQueue() {
        lock.lock();
        try {
            while (!waitingCars.isEmpty()) {
                Car car = waitingCars.peek();
                if (carsOnFerry.size() < maxCapacity && (currentWeight + car.getWeight()) <= maxWeight) {
                    carsOnFerry.add(waitingCars.poll());
                    currentWeight += car.getWeight();
                    logger.info("Car loaded from queue: {}", car);
                } else {
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void startCrossing() {
        lock.lock();
        try {
            if (isCrossing) {
                logger.info("Ferry is already crossing the river.");
                return;
            }

            if (carsOnFerry.isEmpty()) {
                logger.info("Cannot start crossing. Ferry is empty.");
            } else {
                isCrossing = true;
                logger.info("Starting to cross the river.");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Crossing interrupted: ", e);
                }
                isCrossing = false;
                logger.info("Ferry has reached the other side.");
            }
        } finally {
            lock.unlock();
        }
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public Queue<Car> getCarsOnFerry() {
        return carsOnFerry;
    }

    public Queue<Car> getWaitingCars() {
        return waitingCars;
    }
}