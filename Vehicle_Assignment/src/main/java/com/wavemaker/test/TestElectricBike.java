package com.wavemaker.test;

import com.wavemaker.constant.ElectricVehicleDrivingMode;
import com.wavemaker.electric.ElectricBike;
import com.wavemaker.electric.ElectricVehicle;
import com.wavemaker.exception.UnsupportedOperationException;

public class TestElectricBike {
    public static void main(String[] args) {
        ElectricVehicle electricBike = new ElectricBike();

        electricBike.start();
        electricBike.accelerate(20);
        electricBike.switchDrivingMode(ElectricVehicleDrivingMode.ECO.toString());
        System.out.println("Current Mode: " + electricBike.getCurrentMode());
        electricBike.brake(5);
        System.out.println("Current Speed: " + electricBike.getCurrentSpeed() + " km/h");
        System.out.println("Battery Level: " + electricBike.checkBatteryLevel() + "%");
        electricBike.chargeBattery(15);
        System.out.println("Battery Level after charging: " + electricBike.checkBatteryLevel() + "%");
        try {
            electricBike.getFuelLevelInLiters();
        } catch (UnsupportedOperationException exception) {
            System.out.println(exception.getMessage());  // Print the exception message instead of the stack trace
        }
        electricBike.stop();

        /*
              output :
                    Electric Bike started.
                    Electric Bike accelerated to 20.0 km/h.
                    Current Mode: ECO
                    Electric Bike slowed down to 15.0 km/h.
                    Current Speed: 15.0 km/h
                    Battery Level: 96.0%
                    Battery charged to 100.0%.
                    Battery Level after charging: 100.0%
                    Electric Bike does not use fuel.
                    Electric Bike stopped.
         */
    }
}
