package com.ferry.main;

import com.ferry.manager.FerryManager;

import java.util.concurrent.TimeUnit;

public class Main {
    private static final String FILE_PATH = "/cars.txt";
    public static void main(String[] args) {
        FerryManager ferryManager = FerryManager.getInstance();
        ferryManager.startSimulation(FILE_PATH);
        try {
            TimeUnit.SECONDS.sleep(45);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        ferryManager.stopSimulation();
    }
}