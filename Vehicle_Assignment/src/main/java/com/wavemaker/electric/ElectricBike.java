package com.wavemaker.electric;

public class ElectricBike extends ElectricVehicle{
    private static final int noOfWheels = 2;
    private static final String vehicleType = "Electric Bike";

    public ElectricBike() {
        super();
    }

    @Override
    public void chargeBattery(double amount) {
        batteryLevelInPercentage += amount;
        if (batteryLevelInPercentage > 100) batteryLevelInPercentage = 100;
        System.out.println("Battery charged to " + batteryLevelInPercentage + "%.");
    }

    @Override
    public int getNumberOfWheels() {
        return noOfWheels;
    }

    @Override
    public String getVehicleType() {
        return vehicleType;
    }
}
