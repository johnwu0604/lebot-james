package main.object;

import lejos.robotics.SampleProvider;
import main.resource.Constants;

/**
 * An object that represents a Light Sensor
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
     * @param sensor
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
            sensor.fetchSample(data, 0);
            if( data[0] < Constants.LOWER_LIGHT_THRESHOLD ) {
                lineDetected = true;
            }
        }
    }

    /**
     * A method which determines if a line was recently detected
     *
     * @return
     */
    public boolean isLineDetected() {
        return lineDetected;
    }

    /**
     * A method to set our lineDetected boolean value
     *
     * @param lineDetected
     */
    public void setLineDetected( boolean lineDetected ) {
        this.lineDetected = lineDetected;
    }
}
