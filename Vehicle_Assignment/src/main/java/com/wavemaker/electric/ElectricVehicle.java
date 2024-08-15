package com.wavemaker.electric;

import com.wavemaker.Vehicle;
import com.wavemaker.constant.ElectricVehicleDrivingMode;
import com.wavemaker.exception.UnsupportedOperationException;

public abstract class ElectricVehicle implements Vehicle {

    protected double currentSpeed;
    protected boolean isRunning;
    protected double batteryLevelInPercentage;
    protected ElectricVehicleDrivingMode currentMode;
    private static final int UNSUPPORTED_OPERATION_CODE = 405; //method not supported here

    public ElectricVehicle() {
        currentSpeed = 0;
        batteryLevelInPercentage = 100;
        isRunning = false;
        currentMode = ElectricVehicleDrivingMode.ECO;
    }

    @Override
    public void start() {
        isRunning = true;
        System.out.println(getVehicleType() + " started.");
    }

    @Override
    public void stop() {
        isRunning = false;
        currentSpeed = 0;
        System.out.println(getVehicleType() + " stopped.");
    }

    @Override
    public void accelerate(double speedIncrease) {
        if (batteryLevelInPercentage > 0 && isRunning) {
            currentSpeed += speedIncrease;
            batteryLevelInPercentage -= speedIncrease * 0.2;
            if (batteryLevelInPercentage < 0) batteryLevelInPercentage = 0;
            System.out.println(getVehicleType() + " accelerated to " + currentSpeed + " km/h.");
        } else {
            System.out.println("Insufficient battery or vehicle is not running.");
        }
    }

    @Override
    public void brake(double speedDecrease) {
        if (currentSpeed > speedDecrease) {
            currentSpeed -= speedDecrease;
        } else {
            currentSpeed = 0;
        }
        System.out.println(getVehicleType() + " slowed down to " + currentSpeed + " km/h.");
    }

    @Override
    public double getCurrentSpeed() {
        return currentSpeed;
    }

    @Override
    public double getFuelLevelInLiters() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(getVehicleType() + " does not use fuel.", UNSUPPORTED_OPERATION_CODE);
    }

    @Override
    public void refuel(double amount) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(getVehicleType() + " does not use fuel.", UNSUPPORTED_OPERATION_CODE);
    }

    @Override
    public boolean isCarRunning() {
        return isRunning;
    }

    public double checkBatteryLevel() {
        return batteryLevelInPercentage;
    }

    public void switchDrivingMode(String mode) {
        this.currentMode = ElectricVehicleDrivingMode.valueOf(mode);
    }

    public String getCurrentMode() {
        return currentMode.toString();
    }

    public abstract void chargeBattery(double amount);

    public abstract int getNumberOfWheels();

    public abstract String getVehicleType();
}
