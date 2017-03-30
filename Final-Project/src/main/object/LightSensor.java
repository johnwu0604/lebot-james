package main.object;

import lejos.robotics.SampleProvider;
import main.resource.ThresholdConstants;
import main.resource.TimeConstants;

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
     * Main thread. Must use method startRunning() to begin execution
     */
    public void run() {
        while ( true ) {
            // fetch samples
            sensor.fetchSample(data, 0);
            if( data[0] < ThresholdConstants.LINE_DETECTION) {
                lineDetected = true;
            }
            try { Thread.sleep( TimeConstants.LIGHT_SENSOR_READING_PERIOD ); } catch( Exception e ){}
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
        synchronized ( this ) {
            this.lineDetected = lineDetected;
        }
    }


}
