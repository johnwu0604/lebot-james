package main.controller;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import main.resource.FieldConstants;
import main.resource.ShootingConstants;

/**
 * A controller object to launch the ball
 *
 * @author DurhamAbric
 */
public class Launcher {

    // objects
    private Navigator navigator;
    private Odometer odometer;
    private EV3LargeRegulatedMotor leftLaunchMotor, rightLaunchMotor;

    /**
     * Constructor for launcher object
     */
    public Launcher(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Navigator navigator, Odometer odometer){
        this.navigator = navigator;
        this.odometer = odometer;
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

        double deltaX = FieldConstants.TARGET_CENTER_X_COORDINATE - odometer.getX();
        double deltaY = FieldConstants.TARGET_CENTER_Y_COORDINATE - odometer.getY();

        double distance =  navigator.calculateDistanceToPoint( deltaX, deltaY );
        double targetAngle = navigator.calculateMinAngle( deltaX, deltaY );

        navigator.turnRobot( targetAngle );

        return distance;
    }

}
