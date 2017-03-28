package main.controller;

import lejos.hardware.Sound;
import main.Parameters;
import main.object.LightSensor;
import main.object.Square;
import main.resource.*;

/**
 * A class that locates, and retrieves ball from retriever
 *
 * @author Durham Abric
 */
public class BallRetriever {

    // objects
    private Launcher launcher;
    private Navigator navigator;
    private Odometer odometer;
    private LightSensor leftSensor;
    private LightSensor rightSensor;

    // variables
    private boolean alignmentTimedOut = false;
    private boolean aligningLeft = false;
    private boolean aligningRight = false;

    /**
     * Main constuctor for the ball retreiver class
     *
     * @param launcher
     * @param odo
     * @param nav
     */
    public BallRetriever(Launcher launcher, Odometer odo, Navigator nav, LightSensor leftSensor, LightSensor rightSensor ){
        this.launcher = launcher;
        this.navigator = nav;
        this.odometer = odo;
        this.leftSensor = leftSensor;
        this.rightSensor = rightSensor;
    }

    /**
     * A method to retrieve the ball
     */
    public void getBall(){

        approachDispenser();
        navigator.setCorrectionNeeded(false);
        alignToBallDispenser();
        launcher.setLaunchMotorAcceleration(2*ShootingConstants.BALL_LOWERING_ACCELERATION);
        launcher.rotateLaunchMotors(ShootingConstants.BALL_RETRIEVAL_ANGLE);
        Sound.beep(); //Notify instructors we are ready to receive ball

        try { Thread.sleep( 5000 ); } catch( Exception e ){}

//        launcher.setLaunchMotorAcceleration(ShootingConstants.BALL_LOWERING_ACCELERATION);
//        launcher.rotateLaunchMotors(-ShootingConstants.BALL_RETRIEVAL_ANGLE);
//
//        navigator.travelToSquare( odometer.getLastSquare() );
//        navigator.setCorrectionNeeded(true);

    }

    /**
     * A method to approach the dispenser
     *
     * @return
     */
    private void approachDispenser(){
        Square approach = chooseApproach();
        navigator.travelToSquare( approach );
    }

    /**
     * A method to decide which square to use as our approach
     *
     * @return
     */
    private Square chooseApproach(){

        Square approach1 = odometer.getFieldMapper().calculateBallDispenserApproach()[0];
        Square approach2 = odometer.getFieldMapper().calculateBallDispenserApproach()[1];

        double dist1 = Math.hypot(navigator.getComponentDistances(approach1)[0], navigator.getComponentDistances(approach1)[1]);
        double dist2 = Math.hypot(navigator.getComponentDistances(approach2)[0], navigator.getComponentDistances(approach2)[1]);

        if(dist1 <= dist2){
            return approach1;
        } else {
            return approach2;
        }
    }

    /**
     * A method to align our robot to the ball dispenser
     */
    private void alignToBallDispenser() {

        launcher.retractArm();

        Parameters parameters = odometer.getFieldMapper().getParameters();
        String dispenserOrientation = parameters.getBallDispenserOrientation();

        Square firstTurn = null;
        double secondTurn = 0;

        if (dispenserOrientation.equals("N")){
            if (odometer.getCurrentSquare().getSquarePosition()[0] == parameters.getBallDispenserPosition()[0]) {
                firstTurn = odometer.getEastSquare();
                secondTurn = -Math.PI/2;
            } else {
                firstTurn = odometer.getWestSquare();
                secondTurn = Math.PI/2;
            }
        }

        if (dispenserOrientation.equals("S")){
            if (odometer.getCurrentSquare().getSquarePosition()[0] == parameters.getBallDispenserPosition()[0]) {
                firstTurn = odometer.getEastSquare();
                secondTurn = Math.PI/2;
            } else {
                firstTurn = odometer.getWestSquare();
                secondTurn = -Math.PI/2;
            }
        }


        if (dispenserOrientation.equals("E")){
            if (odometer.getCurrentSquare().getSquarePosition()[1] == parameters.getBallDispenserPosition()[1]) {
                firstTurn = odometer.getNorthSquare();
                secondTurn = Math.PI/2;
            } else {
                firstTurn = odometer.getSouthSquare();
                secondTurn = -Math.PI/2;
            }
        }

        if (dispenserOrientation.equals("W")){
            if (odometer.getCurrentSquare().getSquarePosition()[1] == parameters.getBallDispenserPosition()[1]) {
                firstTurn = odometer.getNorthSquare();
                secondTurn = -Math.PI/2;
            } else {
                firstTurn = odometer.getSouthSquare();
                secondTurn = Math.PI/2;
            }
        }

        leftSensor.startRunning();
        rightSensor.startRunning();
        navigator.turnToSquare( firstTurn );
        moveToLine();
        navigator.turnRobot( secondTurn );
        moveToLine();
        navigator.moveDistance( -ThresholdConstants.BALL_RETRIEVAL_DISTANCE );
        leftSensor.stopRunning();
        rightSensor.stopRunning();

    }

    /**
     * A method to align our vehicle to the first line in front
     */
    private void moveToLine() {
        leftSensor.setLineDetected( false );
        rightSensor.setLineDetected( false );

        navigator.driveForwardSlow();
        while ( !isLineDetectedRight() && !isLineDetectedLeft() ) {
            navigator.driveForwardSlow();
        }
        navigator.stop();
        alignToLine();
        navigator.moveDistance( RobotConstants.LIGHT_SENSOR_TO_TRACK_DISTANCE );

    }

    /**
     * A method to align to the vehicle to the current line
     */
    private void alignToLine() {
        double startTime = System.currentTimeMillis();
        while ( !isLineDetectedLeft() && !hasTimedOut( startTime ) ) {
            navigator.rotateLeftMotorForwardSlow();
            aligningLeft = true;
        }
        while ( !isLineDetectedRight() && !hasTimedOut( startTime ) ) {
            navigator.rotateRightMotorForwardSlow();
            aligningRight = true;
        }
        navigator.stopFast();
        if ( alignmentTimedOut ) {
            revertChangesFromTimeOut();
        }
        aligningRight = false;
        aligningLeft = false;
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
     * A method to determine if our alignment has timed out or not
     *
     * @param startTime
     * @return hasTimedOut
     */
    public boolean hasTimedOut( double startTime ) {
        double currentTime = System.currentTimeMillis();
        alignmentTimedOut = currentTime - startTime < TimeConstants.ALIGNMENT_MAX_TIME ? false : true;
        return alignmentTimedOut;
    }

    /**
     * A method to revert our changes from an alignment time out
     */
    public void revertChangesFromTimeOut() {
        double startTime = System.currentTimeMillis();
        if ( aligningLeft ) {
            while ( !hasTimedOut( startTime ) ) {
                navigator.rotateLeftMotorBackwardSlow();
            }
        }
        if ( aligningRight ) {
            while ( !hasTimedOut( startTime ) ) {
                navigator.rotateRightMotorBackwardSlow();
            }
        }
        navigator.stop();
    }

}
