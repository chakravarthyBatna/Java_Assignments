package com.wavemaker.test;

import com.wavemaker.constant.ElectricVehicleDrivingMode;
import com.wavemaker.electric.ElectricCar;
import com.wavemaker.electric.ElectricVehicle;
import com.wavemaker.exception.UnsupportedOperationException;

public class TestElectricCar {
    public static void main(String[] args) {
        ElectricVehicle electricCar = new ElectricCar();

        electricCar.start();
        electricCar.accelerate(30);
        electricCar.switchDrivingMode(ElectricVehicleDrivingMode.SPORTS.toString());
        System.out.println("Current Mode: " + electricCar.getCurrentMode());
        electricCar.brake(10);
        System.out.println("Current Speed: " + electricCar.getCurrentSpeed() + " km/h");
        System.out.println("Battery Level: " + electricCar.checkBatteryLevel() + "%");
        electricCar.chargeBattery(20);
        System.out.println("Battery Level after charging: " + electricCar.checkBatteryLevel() + "%");
        try {
            electricCar.getFuelLevelInLiters();
        } catch (UnsupportedOperationException unsupportedOperationException) {
            System.out.println(unsupportedOperationException.getMessage());
        }
        electricCar.stop();
    }
}
