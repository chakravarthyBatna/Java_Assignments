package com.wavemaker;

public interface Vehicle {
    void start();

    public void stop();

    public void accelerate(double speedIncrease);

    public void brake(double speedDecrease);

    public double getCurrentSpeed();

    public double getFuelLevelInLiters();

    public void refuel(double amount);

    public boolean isCarRunning();

    public int getNumberOfWheels();

    public String getVehicleType();
}
