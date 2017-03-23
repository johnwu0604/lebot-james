package main.controller;

import lejos.hardware.Sound;
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

        if(distanceToTarget <= 4*Constants.SQUARE_LENGTH){
            rotate(Constants.LAUNCH_ROM_4);
            restArm(Constants.LAUNCH_ROM_4);
        }else if (distanceToTarget <= 5*Constants.SQUARE_LENGTH){
            rotate(Constants.LAUNCH_ROM_5);
            restArm(Constants.LAUNCH_ROM_5);
        }else if (distanceToTarget <= 6*Constants.SQUARE_LENGTH){
            rotate(Constants.LAUNCH_ROM_6);
            restArm(Constants.LAUNCH_ROM_6);
        }else if (distanceToTarget <= 7*Constants.SQUARE_LENGTH){
            rotate(Constants.LAUNCH_ROM_7);
            restArm(Constants.LAUNCH_ROM_7);
        }else if (distanceToTarget <= 8*Constants.SQUARE_LENGTH){
            rotate(Constants.LAUNCH_ROM_8);
            restArm(Constants.LAUNCH_ROM_8);
        } else {
            rotate(Constants.LAUNCH_ROM_MAX);
            restArm(Constants.LAUNCH_ROM_MAX);
        }

        //navigator.travelTo(navigator.getOdometer().getCurrentSquare().getCenterCoordinate()[0], navigator.getOdometer().getCurrentSquare().getCenterCoordinate()[1]);
        //navigator.stop();
    }

    public void doBetaDemo(){
        navigator.travelToSquare(navigator.getOdometer().getFieldMapper().getMapping()[1][1]);

        retractArm();
        Sound.beep(); //Notify ball is ready to be placed
        try { Thread.sleep( 5000 ); } catch( Exception e ){}

        navigator.travelToSquare(navigator.getOdometer().getFieldMapper().getMapping()[5][1]);

        launchBall();
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

        double deltaX = Constants.TARGET_CENTER_X_COORDINATE - navigator.getOdometer().getX();
        double deltaY = Constants.TARGET_CENTER_Y_COORDINATE - navigator.getOdometer().getY();

        double distance =  navigator.calculateDistanceToPoint(deltaX, deltaY);
        double targetAngle = navigator.calculateMinAngle(deltaX, deltaY);

        navigator.turnTo(targetAngle);

        return distance;
    }
}
