package com.wavemaker.test;

import com.wavemaker.Bike;
import com.wavemaker.Vehicle;

public class TestBike {
    public static void main(String[] args) {
        Vehicle bike = new Bike();
        System.out.println("Is Bike Running : " + bike.isCarRunning());
        bike.start();
        bike.accelerate(90);
        System.out.println("Is Bike Running : " + bike.isCarRunning());
        bike.brake(50);
        bike.refuel(300);
        System.out.println("Current Fuel in the Bike is : " + bike.getFuelLevelInLiters() + " Liters");
        System.out.println("No of Wheels for this Bike is : " + bike.getNumberOfWheels());

        /*
        output :
            Is Bike Running : false
            Bike started.
            Bike accelerated to 90.0 km/h.
            Is Bike Running : true
            Bike slowed down to 40.0 km/h.
            Car refueled. Fuel level: 7.74920639575376 liters.
            Current Fuel in the Bike is : 7.74920639575376 Liters
            No of Wheels for this Bike is : 2
         */
    }
}
