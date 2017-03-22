package main.controller;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import main.object.LightSensor;
import main.resource.Constants;

/**
 * A controller class for odometer correction
 *
 * @author JohnWu
 */
public class OdometerCorrection extends Thread {

    // objects
    private Navigator navigator;
    private Odometer odometer;
    private LightSensor leftSensor;
    private LightSensor rightSensor;

    /**
     * Our main constructor method
     *
     * @param leftSensor the left facing sensor EV3 object used in the robot
     * @param rightSensor the right facing sensor EV3 object used in the robot
     */
    public OdometerCorrection( Navigator navigator, Odometer odometer, SampleProvider leftSensor, SampleProvider rightSensor ) {
        this.navigator = navigator;
        this.odometer = odometer;
        this.leftSensor = new LightSensor( leftSensor );
        this.rightSensor = new LightSensor( rightSensor );
        this.leftSensor.start();
        this.rightSensor.start();
    }

    /**
     * Main thread
     */
    public void run() {
        while ( true ) {
            if ( isLineDetectedLeft() || isLineDetectedRight() ) {
                odometer.setCorrecting( true );
                while ( !isLineDetectedLeft() ) {
                    navigator.stopRightMotor();
                }
                while ( !isLineDetectedRight() ) {
                    navigator.stopLeftMotor();
                }
                try { Thread.sleep( Constants.LINE_DETECTINO_HOLD_TIME ); } catch( Exception e ){}
                correctOdometerValues();
                navigator.driveForward();
                odometer.setCorrecting( false );
                try { Thread.sleep( Constants.COLOR_SENSOR_HOLD_TIME ); } catch( Exception e ){}
                leftSensor.setLineDetected( false );
                rightSensor.setLineDetected( false );
            }
        }
    }

    /**
     * A method to correct our odometer values
     */
    public void correctOdometerValues() {
        double correctedTheta = calculateCorrectionTheta();
        int currentSquareX = odometer.getCurrentSquare().getSquarePosition()[0];
        int currentSquareY = odometer.getCurrentSquare().getSquarePosition()[1];

        odometer.setTheta( correctedTheta );
        if ( correctedTheta == 0.0 ) {
            odometer.setY( odometer.getCurrentSquare().getNorthLine() - Constants.SENSOR_TO_TRACK_DISTANCE );
            odometer.addPastSquare(odometer.getFieldMapper().getMapping()[currentSquareX][currentSquareY] );
            odometer.setCurrentSquare( odometer.getFieldMapper().getMapping()[currentSquareX][currentSquareY+1] );
        }
        if ( correctedTheta == Math.PI/2 ) {
            odometer.setX( odometer.getCurrentSquare().getEastLine() - Constants.SENSOR_TO_TRACK_DISTANCE );
            odometer.addPastSquare(odometer.getFieldMapper().getMapping()[currentSquareX][currentSquareY] );
            odometer.setCurrentSquare( odometer.getFieldMapper().getMapping()[currentSquareX+1][currentSquareY] );
        }
        if ( correctedTheta == 2*Math.PI ) {
            odometer.setY( odometer.getCurrentSquare().getSouthLine() - Constants.SENSOR_TO_TRACK_DISTANCE );
            odometer.addPastSquare(odometer.getFieldMapper().getMapping()[currentSquareX][currentSquareY] );
            odometer.setCurrentSquare( odometer.getFieldMapper().getMapping()[currentSquareX][currentSquareY-1] );
        }
        if ( correctedTheta == 3*Math.PI/2 ) {
            odometer.setX( odometer.getCurrentSquare().getWestLine() - Constants.SENSOR_TO_TRACK_DISTANCE );
            odometer.addPastSquare(odometer.getFieldMapper().getMapping()[currentSquareX][currentSquareY] );
            odometer.setCurrentSquare( odometer.getFieldMapper().getMapping()[currentSquareX-1][currentSquareY] );
        }
        odometer.setCorrecting( false );
    }

    /**
     * A method to determine if a line was recently detected or not for the left sensor
     *
     * @return whether line has been detected
     */
    public boolean isLineDetectedLeft() {
        return leftSensor.isLineDetected();
    }

    /**
     * A method to determine if a line was recently detected or not for the right sensor
     *
     * @return whether line has been detected
     */
    public boolean isLineDetectedRight() {
        return rightSensor.isLineDetected();
    }

    /**
     * A method which calculates the proper theta to correct to upon reaching a line
     *
     * @return the correct theta value
     */
    public double calculateCorrectionTheta() {
        if ( odometer.getTheta() >= 7*Math.PI/4 && odometer.getTheta() < 2*Math.PI ) {
            return 0.0;
        }
        if ( odometer.getTheta() >= 0 && odometer.getTheta() < Math.PI/4 ) {
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