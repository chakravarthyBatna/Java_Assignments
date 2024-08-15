package com.wavemaker.electric;

import com.wavemaker.constant.ElectricVehicleDrivingMode;

public class ElectricCar extends ElectricVehicle {
    private static final int noOfWheels = 4;
    private static final String vehicleType = "Electric Car";

    public ElectricCar() {
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
