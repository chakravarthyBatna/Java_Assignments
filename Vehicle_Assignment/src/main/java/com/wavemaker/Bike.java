package com.wavemaker;

public class Bike implements Vehicle {
    private double currentSpeed;
    private double fuelLevelInLiters;
    private boolean isBikeRunning;
    private static final int noOfWheels = 2;
    private static final String vehicleType = "Bike";
    private static final double maxFuelLevel = 12; //maxFuelLevel for the Bike is 12 liters we are assuming
    public Bike() {
        fuelLevelInLiters = 5d; //initially start with 5 liters of fuel;
        currentSpeed = 0d;
        isBikeRunning = false;
    }
    @Override
    public void start() {
        isBikeRunning = true;
        System.out.println("Bike started.");
    }

    @Override
    public void stop() {
        isBikeRunning = false;
        currentSpeed = 0;
        System.out.println("Bike stopped.");
    }

    @Override
    public void accelerate(double speedIncrease) {
        if (fuelLevelInLiters <= 0) {
            System.out.println("No Fuel to Run the bike");
        }
        if (isBikeRunning) {
            currentSpeed += speedIncrease;
            System.out.println("Bike accelerated to " + currentSpeed + " km/h.");
        } else {
            System.out.println("Start the bike first!");
        }
    }

    @Override
    public void brake(double speedDecrease) {
        if (currentSpeed > speedDecrease) {
            currentSpeed -= speedDecrease;
        } else {
            currentSpeed = 0;
        }
        System.out.println("Bike slowed down to " + currentSpeed + " km/h.");
    }

    @Override
    public double getCurrentSpeed() {
        return currentSpeed;
    }

    @Override
    public double getFuelLevelInLiters() {
        return fuelLevelInLiters;
    }

    @Override
    public void refuel(double amount) {
        fuelLevelInLiters += convertAmountToFuelInLiters(amount);  //present fuel + person refuels the fuel in bike
        if (fuelLevelInLiters > maxFuelLevel) {
            fuelLevelInLiters = maxFuelLevel; //30 is the maxFuelLevel in a car we are assuming;
        }
        System.out.println("Car refueled. Fuel level: " + fuelLevelInLiters + " liters.");
    }

    @Override
    public boolean isCarRunning() {
        return isBikeRunning;
    }

    @Override
    public int getNumberOfWheels() {
        return noOfWheels;
    }

    @Override
    public String getVehicleType() {
        return vehicleType;
    }

    private double convertAmountToFuelInLiters(double amount) {
        double costPerLiter = 109.1224d; // Cost of 1 liter of fuel in Rs
        return amount / costPerLiter; // Convert the given amount to liters
    }
}

