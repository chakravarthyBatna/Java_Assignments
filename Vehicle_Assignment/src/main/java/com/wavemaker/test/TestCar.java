package com.wavemaker.test;

import com.wavemaker.Car;
import com.wavemaker.Vehicle;

public class TestCar {
    public static void main(String[] args) {
        Vehicle car = new Car();
        System.out.println("Is Car Running : " + car.isCarRunning());
        car.start();
        car.accelerate(90);
        System.out.println("Is Car Running : " + car.isCarRunning());
        car.brake(50);
        car.refuel(300);
        System.out.println("Current Fuel in the car is : " + car.getFuelLevelInLiters() + " Liters");
        System.out.println("No of Wheels for this car is : " + car.getNumberOfWheels());
        /*
        output :
            Is Car Running : false
            Car started.
            Car accelerated to 90.0 km/h.
            Is Car Running : true
            Car slowed down to 40.0 km/h.
            Car refueled. Fuel level: 14.60095295448315 liters.
            Current Fuel in the car is : 14.60095295448315 Liters
            No of Wheels for this car is : 4
        */
    }
}
