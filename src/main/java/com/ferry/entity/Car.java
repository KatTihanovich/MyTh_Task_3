package com.ferry.entity;

public class Car {
    private final String type;
    private final int weight;

    public Car(String type, int weight) {
        this.type = type;
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("Car[type='%s', weight=%d]", type, weight);
    }
}