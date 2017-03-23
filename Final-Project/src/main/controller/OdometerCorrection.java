package main.controller;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import main.object.LightSensor;
import main.resource.RobotConstants;
import main.resource.TimeConstants;

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

    // variables
    private boolean hasTimedOut = false;
    private boolean correctingLeft = false;
    private boolean correctingRight = false;
    private boolean running = true;

    /**
     * Our main constructor method
     *
     * @param leftSensor the left facing sensor EV3 object used in the robot
     * @param rightSensor the right facing sensor EV3 object used in the robot
     */
    public OdometerCorrection( Navigator navigator, Odometer odometer, LightSensor leftSensor, LightSensor rightSensor ) {
        this.navigator = navigator;
        this.navigator.setOdometerCorrection( this );
        this.odometer = odometer;
        this.leftSensor = leftSensor;
        this.rightSensor = rightSensor;
        this.leftSensor.start();
        this.rightSensor.start();
    }

    /**
     * Main thread
     */
    public void run() {
        while ( true ) {
            if ( running ) {
                if ( isLineDetectedLeft() || isLineDetectedRight() ) {
                    odometer.setCorrecting( true );
                    doCorrection();
                    odometer.setCorrecting( false );
                    try { Thread.sleep( TimeConstants.COLOR_SENSOR_HOLD_TIME ); } catch( Exception e ){}
                    hasTimedOut = false;
                    correctingLeft = false;
                    correctingRight = false;
                    leftSensor.setLineDetected( false );
                    rightSensor.setLineDetected( false );
                }
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
            odometer.setY( odometer.getCurrentSquare().getNorthLine() - RobotConstants.LIGHT_SENSOR_TO_TRACK_DISTANCE);
            odometer.addPastSquare(odometer.getFieldMapper().getMapping()[currentSquareX][currentSquareY] );
            odometer.setCurrentSquare( odometer.getFieldMapper().getMapping()[currentSquareX][currentSquareY+1] );
        }
        if ( correctedTheta == Math.PI/2 ) {
            odometer.setX( odometer.getCurrentSquare().getEastLine() - RobotConstants.LIGHT_SENSOR_TO_TRACK_DISTANCE);
            odometer.addPastSquare(odometer.getFieldMapper().getMapping()[currentSquareX][currentSquareY] );
            odometer.setCurrentSquare( odometer.getFieldMapper().getMapping()[currentSquareX+1][currentSquareY] );
        }
        if ( correctedTheta == Math.PI ) {
            odometer.setY( odometer.getCurrentSquare().getSouthLine() - RobotConstants.LIGHT_SENSOR_TO_TRACK_DISTANCE);
            odometer.addPastSquare(odometer.getFieldMapper().getMapping()[currentSquareX][currentSquareY] );
            odometer.setCurrentSquare( odometer.getFieldMapper().getMapping()[currentSquareX][currentSquareY-1] );
        }
        if ( correctedTheta == 3*Math.PI/2 ) {
            odometer.setX( odometer.getCurrentSquare().getWestLine() - RobotConstants.LIGHT_SENSOR_TO_TRACK_DISTANCE);
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

    /**
     * A method to determine if our correction has timed out or not
     *
     * @param startTime
     * @return whether the correction has timed out
     */
    public boolean hasTimedOut( double startTime ) {
        double currentTime = System.currentTimeMillis();
        hasTimedOut = currentTime - startTime < TimeConstants.CORRECTION_MAX_TIME ? false : true;
        return hasTimedOut;
    }

    /**
     * A method to revert our changes from a correction time out
     */
    public void revertChangesFromTimeOut() {
        double startTime = System.currentTimeMillis();
        if ( correctingLeft ) {
            while ( !hasTimedOut( startTime ) ) {
                navigator.rotateLeftMotorBackward();
                navigator.stopRightMotor();
            }
        }
        if ( correctingRight ) {
            while ( !hasTimedOut( startTime ) ) {
                navigator.rotateRightMotorBackward();
                navigator.stopLeftMotor();
            }
        }
        navigator.driveForward();
    }

    /**
     * A method to do our odometry correction
     */
    public void doCorrection() {
        boolean wentIntoTimedOutMethod = false;
        double startTime = System.currentTimeMillis();
        while ( !isLineDetectedLeft() && !hasTimedOut( startTime ) ) {
            navigator.stopRightMotor();
            correctingLeft = true;
        }
        while ( !isLineDetectedRight() && !hasTimedOut( startTime ) ) {
            navigator.stopLeftMotor();
            correctingRight = true;
        }
        if ( hasTimedOut ) {
            wentIntoTimedOutMethod = true;
            revertChangesFromTimeOut();
        } else {
            try { Thread.sleep( TimeConstants.LINE_DETECTION_HOLD_TIME); } catch( Exception e ){}
        }
        double thetaAfterTimeOut = odometer.getTheta();
        navigator.driveForward();
        correctOdometerValues();
        if ( wentIntoTimedOutMethod ) {
            odometer.setTheta( thetaAfterTimeOut );
        }
    }

    /**
     * A method to temporarily stop our thread
     */
    public void stopRunning() {
        leftSensor.stopRunning();
        rightSensor.stopRunning();
        running = false;
    }

    /**
     * A method to restart our thread
     */
    public void startRunning() {
        leftSensor.startRunning();
        rightSensor.startRunning();
        running = true;
    }

}