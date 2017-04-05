package main.controller;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import main.Parameters;
import main.object.Square;
import main.resource.*;

/**
 * A controller object to launch the ball
 *
 * @author DurhamAbric
 */
public class Launcher {

    // objects
    private Navigator navigator;
    private Odometer odometer;
    private OdometerCorrection odometerCorrection;
    private EV3LargeRegulatedMotor leftLaunchMotor, rightLaunchMotor;

    // variables
    private boolean aligningLeft = false;
    private boolean aligningRight = true;
    private boolean alignmentTimedOut = false;

    /**
     * Constructor for launcher object
     */
    public Launcher(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Navigator navigator, Odometer odometer,
                    OdometerCorrection odometerCorrection){
        this.navigator = navigator;
        this.odometer = odometer;
        this.odometerCorrection = odometerCorrection;
        this.leftLaunchMotor = leftMotor;
        this.rightLaunchMotor = rightMotor;
    }

    /**
     * A method to aim at target and launch ball
     */
    public void launchBall(){

        setLaunchMotorAcceleration( ShootingConstants.LAUNCH_MOTOR_ACCELERATION );

        double leftSpeed = leftLaunchMotor.getMaxSpeed();
        double rightSpeed = rightLaunchMotor.getMaxSpeed();

        if (leftSpeed <= rightSpeed){
            setLaunchMotorSpeed( (int) leftSpeed );
        } else {
            setLaunchMotorSpeed( (int) rightSpeed );
        }

        double distanceToTarget =  alignToTarget(); //faces target and returns distance to target

        if(distanceToTarget <= 4*FieldConstants.SQUARE_LENGTH){
            setLaunchMotorSpeed(ShootingConstants.LAUNCH_SPEED_5);
            rotateLaunchMotors(ShootingConstants.LAUNCH_ROM_4);
            restArm(ShootingConstants.LAUNCH_ROM_4);
        }else if (distanceToTarget <= 5*FieldConstants.SQUARE_LENGTH){
            setLaunchMotorSpeed(ShootingConstants.LAUNCH_SPEED_5);
            rotateLaunchMotors(ShootingConstants.LAUNCH_ROM_5);
            restArm(ShootingConstants.LAUNCH_ROM_5);
        }else if (distanceToTarget <= 6*FieldConstants.SQUARE_LENGTH){
            setLaunchMotorSpeed(ShootingConstants.LAUNCH_SPEED_6);
            rotateLaunchMotors(ShootingConstants.LAUNCH_ROM_6);
            restArm(ShootingConstants.LAUNCH_ROM_6);
        }else if (distanceToTarget <= 7*FieldConstants.SQUARE_LENGTH){
            setLaunchMotorSpeed(ShootingConstants.LAUNCH_SPEED_7);
            rotateLaunchMotors(ShootingConstants.LAUNCH_ROM_7);
            restArm(ShootingConstants.LAUNCH_ROM_7);
        }else if (distanceToTarget <= 8*FieldConstants.SQUARE_LENGTH){
            setLaunchMotorSpeed(ShootingConstants.LAUNCH_SPEED_8);
            rotateLaunchMotors(ShootingConstants.LAUNCH_ROM_8);
            restArm(ShootingConstants.LAUNCH_ROM_8);
        } else {
            setLaunchMotorSpeed(ShootingConstants.LAUNCH_SPEED_MAX);
            rotateLaunchMotors(ShootingConstants.LAUNCH_ROM_MAX);
            restArm(ShootingConstants.LAUNCH_ROM_MAX);
        }

        navigator.setShootingPositionExecuted(odometer.getCurrentSquare());
    }

    /**
     * A method to bring arm down from rest/starting position
     */
    public void retractArm(){
        setLaunchMotorAcceleration(ShootingConstants.LAUNCH_MOTOR_RETRACTION_ACCELERATION);
        setLaunchMotorSpeed(ShootingConstants.LAUNCH_MOTOR_RETRACTION_SPEED);
        rotateLaunchMotors(ShootingConstants.LAUNCH_RETRACTION_ROM);
        stopLaunchMotors();
    }

    /**
     * A method that returns arm to starting/rest position
     * @param launchROM angle arm rotated during firing
     */
    public void restArm(int launchROM){
        setLaunchMotorAcceleration(ShootingConstants.LAUNCH_MOTOR_RETRACTION_ACCELERATION);
        setLaunchMotorSpeed(ShootingConstants.LAUNCH_MOTOR_RETRACTION_SPEED);
        rotateLaunchMotors(Math.abs(launchROM + ShootingConstants.LAUNCH_RETRACTION_ROM));
    }


    /**
     * A method to set acceleration of launch motors
     * @param acceleration
     */
    public void setLaunchMotorAcceleration(int acceleration){
        leftLaunchMotor.setAcceleration(acceleration);
        rightLaunchMotor.setAcceleration(acceleration);
    }

    /**
     * A method to set speed of launch motors
     * @param speed
     */
    public void setLaunchMotorSpeed(int speed){
        leftLaunchMotor.setSpeed(speed);
        rightLaunchMotor.setSpeed(speed);
    }

    /**
     * A method to rotate launch motors
     * @param degrees
     */
    public void rotateLaunchMotors(int degrees){
        leftLaunchMotor.rotate(degrees, true);
        rightLaunchMotor.rotate(degrees, false);
    }

    /**
     * A method to stop launch motors
     */
    private void stopLaunchMotors(){
        leftLaunchMotor.stop(true);
        rightLaunchMotor.stop(false);
    }

    /**
     * A method to turnRobot to face target
     * @return distance ball must travel to target
     */
    private double alignToTarget(){

        navigator.setCorrectionNeeded( false );

        double angleToAlignEast = navigator.calculateMinAngle( odometer.getEastSquare().getCenterCoordinate()[0] - odometer.getX(),
                odometer.getEastSquare().getCenterCoordinate()[1] - odometer.getY() );
        navigator.turnRobot( angleToAlignEast );
        align();
        navigator.turnRobot( -Math.PI/2 );
        align();
        odometer.setTheta( 0 );
        odometer.setX( odometer.getCurrentSquare().getCenterCoordinate()[0] );
        odometer.setY( odometer.getCurrentSquare().getCenterCoordinate()[1] );

        double deltaX = FieldConstants.TARGET_CENTER_X_COORDINATE - odometer.getX();
        double deltaY = FieldConstants.TARGET_CENTER_Y_COORDINATE - odometer.getY();

        double distance =  navigator.calculateDistanceToPoint( deltaX, deltaY );
        double targetAngle = navigator.calculateMinAngle( deltaX, deltaY );

        navigator.turnRobot( targetAngle );

        navigator.setCorrectionNeeded( true );

        return distance;
    }

    /**
     * A method to align our vehicle to the first line in front
     */
    private void align() {
        odometerCorrection.resetSensors();
        navigator.driveForwardSlow();
        while ( !odometerCorrection.isLineDetectedRight() && !odometerCorrection.isLineDetectedLeft() ) {
            navigator.driveForwardSlow();
        }
        navigator.stopFast();
        correctAlignment();
        navigator.moveDistance( RobotConstants.LIGHT_SENSOR_TO_TRACK_DISTANCE - 0.5 );
        navigator.moveDistance( -FieldConstants.SQUARE_LENGTH/2 );
    }

    /**
     * A method to align to the vehicle to the current line
     */
    private void correctAlignment() {
        double startTime = System.currentTimeMillis();
        while ( !odometerCorrection.isLineDetectedLeft() && !hasTimedOut( startTime ) ) {
            navigator.rotateLeftMotorForwardSlow();
            aligningLeft = true;
        }
        while ( !odometerCorrection.isLineDetectedRight() && !hasTimedOut( startTime ) ) {
            navigator.rotateRightMotorForwardSlow();
            aligningRight = true;
        }
        if ( alignmentTimedOut ) {
            navigator.stopFast();
            revertChangesFromTimeOut();
        } else {
            startTime = System.currentTimeMillis();
            if ( aligningLeft ) {
                while ( !holdTimedOut( startTime ) ) {
                    navigator.rotateLeftMotorForwardSlow();
                }
            }
            if ( aligningRight ) {
                while ( !holdTimedOut( startTime ) ) {
                    navigator.rotateRightMotorForwardSlow();
                }
            }
        }
        navigator.stopFast();
        aligningRight = false;
        aligningLeft = false;
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
     * A method to determine if our alignment has timed out or not
     *
     * @param startTime
     * @return hasTimedOut
     */
    public boolean holdTimedOut( double startTime ) {
        double currentTime = System.currentTimeMillis();
        alignmentTimedOut = currentTime - startTime < TimeConstants.ALIGNMENT_HOLD_TIME ? false : true;
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
