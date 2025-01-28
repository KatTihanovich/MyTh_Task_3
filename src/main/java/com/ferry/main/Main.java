package com.ferry.main;

import com.ferry.manager.FerryManager;

public class Main {
    public static void main(String[] args) {
        FerryManager ferryManager = FerryManager.getInstance();

        ferryManager.startSimulation("/cars.txt");

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        ferryManager.stopSimulation();
    }
}