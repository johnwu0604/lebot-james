package main.object;

import lejos.hardware.Sound;
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
    private volatile boolean running = false;

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
     * Main thread. Must use method startRunning() to begin execution
     */
    public void run() {
        while ( true ) {
            // pause thread if it is not running
            if ( !running ) {
                try { pauseThread(); } catch ( Exception e ) {}
            }
            // fetch data samples
            sensor.fetchSample( data, 0 );
            try { Thread.sleep( TimeConstants.ULTRASONICSENSOR_SENSOR_READING_PERIOD ); } catch( Exception e ){}
        }
    }

    /**
     * A method to temporarily pause our thread
     */
    public void pauseThread() throws InterruptedException {
        synchronized ( this ) {
            wait();
        }
    }

    /**
     * A method to temporarily stop our thread
     */
    public void stopRunning() {
        synchronized ( this ) {
            running = false;
        }
    }

    /**
     * A method to restart our thread
     */
    public void startRunning() {
        synchronized ( this ) {
            running = true;
            notifyAll();
        }
    }

    /**
     * A method which filters our data for the front sensor (distance to track)
     *
     * @return filtered distance data
     */
    public float getFilteredFrontSensorData() {
        float distance = data[0]*100 + RobotConstants.FRONT_US_SENSOR_TO_TRACK_DISTANCE;
        return distance > ThresholdConstants.ULTRASONICSENSOR_MAX_DISTANCE ? ThresholdConstants.ULTRASONICSENSOR_MAX_DISTANCE : distance;
    }


}