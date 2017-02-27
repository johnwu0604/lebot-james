package main.controller;

import lejos.robotics.SampleProvider;

/**
 * A controller to localize our robot.
 */
public class Localizer {

    // objects
    private Odometer odometer;
    private SampleProvider sensor;
    private Navigator navigator;

    // variables
    private float[] sensorData;

    /**
     * Our default constructor
     *
     * @param odometer
     * @param sensor
     * @param sensorData
     * @param navigator
     */
    public Localizer( Odometer odometer, SampleProvider sensor, float[] sensorData, Navigator navigator ) {
        this.odometer = odometer;
        this.sensor = sensor;
        this.sensorData = sensorData;
        this.navigator = navigator;
    }

    /**
     * A method which filters our data for the distance
     *
     * @return
     */
    private float getFilteredData() {
        sensor.fetchSample( sensorData, 0 );
        float distance = sensorData[0]*100;
        return distance > 100 ? 100 : distance;
    }

}
