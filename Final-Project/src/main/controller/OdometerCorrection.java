package main.controller;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import lejos.utility.Matrix;
import main.resource.Constants;

/**
 * Created by JohnWu on 2017-03-12.
 */
public class OdometerCorrection extends Thread {

    // objects
    private Navigator navigator;
    private Odometer odometer;
    private SampleProvider leftSensor;
    private SampleProvider rightSensor;

    // variables
    private float[] leftSensorData;
    private  float[] rightSensorData;

    /**
     * Our main constructor method
     *
     * @param leftSensor
     * @param rightSensor
     */
    public OdometerCorrection( Navigator navigator, Odometer odometer, SampleProvider leftSensor, SampleProvider rightSensor ) {
        this.navigator = navigator;
        this.odometer = odometer;
        this.leftSensor = leftSensor;
        this.rightSensor = rightSensor;
        this.leftSensorData = new float[leftSensor.sampleSize()];
        this.rightSensorData = new float[rightSensor.sampleSize()];
    }

    /**
     * Main thread
     */
    public void run() {
        while ( true ) {
            if (isLineDetectedLeft()) {
                navigator.stopMotors();
                odometer.setCorrecting(true);
                Sound.buzz();
                while ( !isLineDetectedRight() ) {
                    navigator.rotateRightMotorForward();
                }
                navigator.stopMotors();
                odometer.setTheta( calculateCorrectionTheta() );
                odometer.setCorrecting( false );
                try { Thread.sleep( Constants.COLOR_SENSOR_HOLD_TIME ); } catch( Exception e ){}
            }
            if (isLineDetectedRight()) {
                navigator.stopMotors();
                odometer.setCorrecting( true);
                Sound.buzz();
                while ( !isLineDetectedLeft() ) {
                    navigator.rotateLeftMotorForward();
                }
                navigator.stopMotors();
                odometer.setTheta( calculateCorrectionTheta() );
                odometer.setCorrecting( false );
                try { Thread.sleep( Constants.COLOR_SENSOR_HOLD_TIME ); } catch( Exception e ){}
            }
        }
    }

    /**
     * A method to determine if a line is detected or not for the left sensor
     *
     * @return
     */
    public boolean isLineDetectedLeft() {
        leftSensor.fetchSample(leftSensorData, 0);
        if( leftSensorData[0] < Constants.LOWER_LIGHT_THRESHOLD ) {
            return true;
        }
        return false;
    }

    /**
     * A method to determine if a line is detected or not for the right sensor
     *
     * @return
     */
    public boolean isLineDetectedRight() {
        rightSensor.fetchSample(rightSensorData, 0);
        if( rightSensorData[0] < Constants.LOWER_LIGHT_THRESHOLD ) {
            return true;
        }
        return false;
    }

    public double calculateCorrectionTheta() {
        if ( odometer.getTheta() >= 7*Math.PI/4 && odometer.getTheta() < 2*Math.PI ) {
            return 0.0;
        }
        if ( odometer.getTheta() >= 0 && odometer.getTheta() < Math.PI/2 ) {
            return 0.0;
        }
        if ( odometer.getTheta() >= Math.PI/4 && odometer.getTheta() < 3*Math.PI/4 ) {
            return Math.PI/2;
        }
        if ( odometer.getTheta() >= 3*Math.PI/4 && odometer.getTheta() < 5*Math.PI/4 ) {
            return Math.PI;
        }
        if ( odometer.getTheta() >= 5*Math.PI/4 && odometer.getTheta() < 7*Math.PI/4 ) {
            return 3*Math.PI/2;
        }
        return 0.0;
    }

}