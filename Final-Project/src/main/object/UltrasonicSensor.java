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
    private boolean running = true;

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
            if ( running ) {
                sensor.fetchSample( data, 0 );
                try { Thread.sleep( TimeConstants.ULTRASONICSENSOR_SENSOR_READING_PERIOD ); } catch( Exception e ){}
            }
        }
    }

    /**
     * A method that stops our thread
     */
    public void stopRunning() {
        running = false;
    }

    /**
     * A method which filters our data for the distance
     *
     * @return filtered distance data
     */
    public float getFilteredSensorData() {
        float distance = data[0]*100 + RobotConstants.FRONT_US_SENSOR_TO_TRACK_DISTANCE;
        return distance > ThresholdConstants.ULTRASONICSENSOR_MAX_DISTANCE ? ThresholdConstants.ULTRASONICSENSOR_MAX_DISTANCE : distance;
    }

}
