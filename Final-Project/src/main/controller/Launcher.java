package main.controller;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import main.resource.Constants;

public class Launcher {

    private Navigator navigator;
    private EV3LargeRegulatedMotor leftLaunchMotor, rightLaunchMotor;

    /**
     * Constructor for launcher object, controls launching the ball
     */
    public Launcher(EV3LargeRegulatedMotor left, EV3LargeRegulatedMotor right, Navigator nav){
        this.navigator = nav;
        this.leftLaunchMotor = left;
        this.rightLaunchMotor = right;
    }

    /**
     * method to bring arm down from rest/starting position
     */
    public void retractArm(){
        setAcceleration(Constants.LAUNCH_MOTOR_RETRACTION_ACCELERATION);
        setSpeed(Constants.LAUNCH_MOTOR_RETRACTION_SPEED);
        rotate(Constants.LAUNCH_RETRACTION_ROM);
        stop();
    }

    /**
     * method to returns arm to starting/rest position
     * @param launchROM is angle arm rotated during firing
     */
    public void restArm(int launchROM){
        setAcceleration(Constants.LAUNCH_MOTOR_RETRACTION_ACCELERATION);
        setSpeed(Constants.LAUNCH_MOTOR_RETRACTION_SPEED);
        rotate(Math.abs(launchROM + Constants.LAUNCH_RETRACTION_ROM));
    }

    /**
     * method to aim at target and launch ball
     */
    public void launchBall(){
        setAcceleration(Constants.LAUNCH_MOTOR_ACCELERATION);

        double leftSpeed = leftLaunchMotor.getMaxSpeed();
        double rightSpeed = rightLaunchMotor.getMaxSpeed();

        if (leftSpeed <= rightSpeed){
            setSpeed((int)leftSpeed);
        } else {
            setSpeed((int) rightSpeed);
        }

        double distanceToTarget =  alignToTarget(); //faces target and returns distance to target

        if(distanceToTarget <= 5*Constants.SQUARE_LENGTH){
            rotate(Constants.LAUNCH_ROM_5);
            restArm(Constants.LAUNCH_ROM_5);
        }else if (distanceToTarget <= 6*Constants.SQUARE_LENGTH){
            rotate(Constants.LAUNCH_ROM_6);
            restArm(Constants.LAUNCH_ROM_6);
        }else if (distanceToTarget <= 7*Constants.SQUARE_LENGTH){
            rotate(Constants.LAUNCH_ROM_7);
            restArm(Constants.LAUNCH_ROM_7);
        }else{
            rotate(Constants.LAUNCH_ROM_8);
            restArm(Constants.LAUNCH_ROM_8);
        }

    }

    /**
     * method to set acceleration of launch motors
     * @param acceleration
     */
    private void setAcceleration(int acceleration){
        leftLaunchMotor.setAcceleration(acceleration);
        rightLaunchMotor.setAcceleration(acceleration);
    }

    /**
     * method to set speed of launch motors
     * @param speed
     */
    private void setSpeed(int speed){
        leftLaunchMotor.setSpeed(speed);
        rightLaunchMotor.setSpeed(speed);
    }

    /**
     * method to rotate launch motors
     * @param degrees
     */
    private void rotate(int degrees){
        leftLaunchMotor.rotate(degrees, true);
        rightLaunchMotor.rotate(degrees, false);
    }

    /**
     * method to stop launch motors
     */
    private void stop(){
        leftLaunchMotor.stop(true);
        rightLaunchMotor.stop(false);
    }

    /**
     * method to turn to face target
     * @return distance ball must travel to target
     */
    private double alignToTarget(){
        double distance =  navigator.calculateDistanceToPoint(Constants.TARGET_CENTER_X_COORDINATE, Constants.TARGET_CENTER_Y_COORDINATE);
        double targetAngle = navigator.calculateMinAngle(Constants.TARGET_CENTER_X_COORDINATE, Constants.TARGET_CENTER_Y_COORDINATE);

        navigator.turnTo(targetAngle);

        return distance;
    }
}
