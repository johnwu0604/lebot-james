package main.object;

import lejos.robotics.SampleProvider;
import main.resource.ThresholdConstants;
import main.resource.RobotConstants;
import main.resource.TimeConstants;

/**
 * An object class that represents an ultrasonic sensor
 *
 * @author JohnWu
 */
public class UltrasonicSensor extends Thread {

    // objects
    private SampleProvider sensor;

    // variables
    private float[] data;

    /**
     * Our main constructor method
     *
     * @param sensor the ultrasonic sensor EV3 object used in the robot
     */
    public UltrasonicSensor( SampleProvider sensor ) {
        this.sensor = sensor;
        this.data = new float[sensor.sampleSize()];
    }

    /**
     * Main thread
     */
    public void run() {
        while ( true ) {
            sensor.fetchSample( data, 0 );
            try { Thread.sleep( TimeConstants.ULTRASONICSENSOR_SENSOR_READING_PERIOD ); } catch( Exception e ){}
        }
    }

    /**
     * A method which filters our data for the distance in terms of localization (distance to track)
     *
     * @return filtered distance data
     */
    public float getFilteredSensorDataLocalization() {
        float distance = data[0]*100 + RobotConstants.FRONT_US_SENSOR_TO_TRACK_DISTANCE;
        return distance > ThresholdConstants.ULTRASONICSENSOR_MAX_DISTANCE ? ThresholdConstants.ULTRASONICSENSOR_MAX_DISTANCE : distance;
    }

    /**
     * A method which filters our data for the distance in terms of obstacle avoidance
     *
     * @return filtered distance data
     */
    public float getFilteredSensorDataAvoidance() {
        float distance = data[0]*100;
        return distance > ThresholdConstants.ULTRASONICSENSOR_MAX_DISTANCE ? ThresholdConstants.ULTRASONICSENSOR_MAX_DISTANCE : distance;
    }


}
