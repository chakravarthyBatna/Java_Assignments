package com.wavemaker;

public class Car implements Vehicle {
    private double currentSpeed;
    private double fuelLevelInLiters;
    private boolean isCarRunning;
    private static final int noOfWheels = 4;
    private static final String vehicleType = "Car";
    private static final double maxFuelLevel = 30;  //30 liters is the max fuel for this car

    public Car() {
        currentSpeed = 0;  //0 kmph;
        fuelLevelInLiters = 12; //initially we start with 12 liters of fuel
        isCarRunning = false;
    }

    @Override
    public void start() {
        isCarRunning = true;
        System.out.println("Car started.");
    }

    @Override
    public void stop() {
        isCarRunning = false;
        currentSpeed = 0;
        System.out.println("Car stopped.");
    }

    @Override
    public void accelerate(double speedIncrease) {
        if (fuelLevelInLiters <= 0) {
            System.out.println("No Fuel to Run the Car");
        }
        if (isCarRunning) {
            currentSpeed += speedIncrease;
            System.out.println("Car accelerated to " + currentSpeed + " km/h.");
        } else {
            System.out.println("Start the car first!");
        }
    }

    @Override
    public void brake(double speedDecrease) {
        if (currentSpeed > speedDecrease) {
            currentSpeed -= speedDecrease;
        } else {
            currentSpeed = 0;
        }
        System.out.println("Car slowed down to " + currentSpeed + " km/h.");
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
        fuelLevelInLiters += convertAmountToFuelInLiters(amount);  //present fuel + person refuels the fuel in car
        if (fuelLevelInLiters > maxFuelLevel) {
            fuelLevelInLiters = maxFuelLevel; //30 is the maxFuelLevel in a car we are assuming;
        }
        System.out.println("Car refueled. Fuel level: " + fuelLevelInLiters + " liters.");
    }

    @Override
    public boolean isCarRunning() {
        return isCarRunning;
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
        double costPerLiter = 115.34234; // Cost of 1 liter of fuel in Rs
        return amount / costPerLiter; // Convert the given amount to liters
    }
}

