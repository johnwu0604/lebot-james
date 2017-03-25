package main.controller;

import main.object.UltrasonicSensor;

/**
 * A controller class that maps obstacles along the field
 */
public class ObstacleAvoider extends Thread {

    // objects
    private UltrasonicSensor leftSensor;
    private UltrasonicSensor frontSensor;
    private Odometer odometer;

    // variables
    private volatile boolean running;

    /**
     * Main constructor class for our obstacle avoider
     *
     * @param leftSensor
     * @param frontSensor
     * @param odometer
     */
    public ObstacleAvoider( UltrasonicSensor leftSensor, UltrasonicSensor frontSensor, Odometer odometer ) {
        this.leftSensor = leftSensor;
        this.frontSensor = frontSensor;
        this.odometer = odometer;
    }

    /**
     * Our main thread
     */
    public void run() {
        while ( true ) {
            while ( running ) {

            }
        }
    }

    /**
     * A method to temporarily start our thread
     */
    public void startRunning() {
        this.running = true;
    }

    /**
     * A method to temporarily stop our thread
     */
    public void stopRunning() {
        this.running = false;
    }



}
