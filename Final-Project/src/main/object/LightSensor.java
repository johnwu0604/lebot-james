package main.object;

import lejos.robotics.SampleProvider;
import main.resource.ThresholdConstants;

/**
 * An object that represents a Light Sensor
 *
 * @author JohnWu
 */
public class LightSensor extends Thread {

    // objects
    private SampleProvider sensor;

    // variables
    private float[] data;
    private boolean lineDetected = false;
    private boolean running = true;

    /**
     * Our main constructor method
     *
     * @param sensor the light sensor EV3 object used in the vehicle
     */
    public LightSensor( SampleProvider sensor ) {
        this.sensor = sensor;
        this.data = new float[sensor.sampleSize()];
    }

    /**
     * Main thread
     */
    public void run() {
        while ( true ) {
            if ( running ) {
                sensor.fetchSample(data, 0);
                if( data[0] < ThresholdConstants.LINE_DETECTION) {
                    lineDetected = true;
                }
            }
        }
    }

    /**
     * A method which determines if a line was recently detected
     *
     * @return whether a line was detected
     */
    public boolean  isLineDetected() {
        return lineDetected;
    }

    /**
     * A method to set our lineDetected boolean value
     *
     * @param lineDetected the boolean value for line detection
     */
    public void setLineDetected( boolean lineDetected ) {
        this.lineDetected = lineDetected;
    }

    /**
     * A method to temporarily stop our thread
     */
    public void stopRunning() {
        running = false;
    }

    /**
     * A method to restart our thread
     */
    public void startRunning() {
        running = true;
    }


}
