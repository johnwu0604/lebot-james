package main.controller;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import main.resource.Constants;

/**
 * Navigator object used to navigate the vehicle.
 *
 * @author JohnWu
 */
public class Navigator extends Thread {

    // objects
    private Odometer odometer;
    private EV3LargeRegulatedMotor leftMotor, rightMotor;

    // variables
    private double travellingToX;
    private double travellingToY;

    /**
     * Default constructor for Navigator object.
     *
     * @param leftMotor
     * @param rightMotor
     * @param odometer
     */
    public Navigator( EV3LargeRegulatedMotor leftMotor , EV3LargeRegulatedMotor rightMotor , Odometer odometer ) {
        this.odometer = odometer;
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
    }

    /**
     * Main run method
     */
    public void run() {

    }


    /**
     * A method to drive our vehicle to a certain cartesian coordinate.
     *
     * @param x X-Coordinate
     * @param y Y-Coordinate
     */
    public void travelToPerpendicular( double x , double y ) {
        travellingToX = x;
        travellingToY = y;

        double deltaX = x - odometer.getX();
        double deltaY = y - odometer.getY();

        travelToDirect( odometer.getX() + deltaX, odometer.getY() );
        travelToDirect( odometer.getX(), odometer.getY() + deltaY );

    }

    /**
     * A method to drive our vehicle directly to a certain cartesian coordinate.
     *
     * @param x X-Coordinate
     * @param y Y-Coordinate
     */
    public void travelToDirect( double x , double y ) {
        double deltaX = x - odometer.getX();
        double deltaY = y - odometer.getY();

        // turn to the minimum angle
        turnTo(calculateMinAngle(deltaX, deltaY));

        // move to the specified point
        double distanceToPoint = calculateDistanceToPoint(deltaX, deltaY);
        leftMotor.setAcceleration(Constants.VEHICLE_ACCELERATION);
        rightMotor.setAcceleration(Constants.VEHICLE_ACCELERATION);
        leftMotor.setSpeed(Constants.VEHICLE_FORWARD_SPEED_HIGH);
        rightMotor.setSpeed(Constants.VEHICLE_FORWARD_SPEED_HIGH);
        leftMotor.rotate(convertDistance(distanceToPoint), true);
        rightMotor.rotate(convertDistance(distanceToPoint), false);

        leftMotor.stop(true);
        rightMotor.stop(true);
    }


    /**
     * A method to turn our vehicle to a certain angle.
     *
     * @param theta
     */
    public void turnTo( double theta ) {
        leftMotor.setSpeed( Constants.VEHICLE_ROTATE_SPEED );
        rightMotor.setSpeed( Constants.VEHICLE_ROTATE_SPEED );
        if( theta < 0 ) { // if angle is negative, turn to the left
            leftMotor.rotate( -convertAngle( -(theta*180)/Math.PI ) , true );
            rightMotor.rotate( convertAngle( -(theta*180)/Math.PI ) , false );
        }
        else { // angle is positive, turn to the right
            leftMotor.rotate( convertAngle( (theta*180)/Math.PI ) , true);
            rightMotor.rotate( -convertAngle( (theta*180)/Math.PI ) , false);
        }
    }

    /**
     * A method to rotate the left motor forward
     */
    public void rotateLeftMotorForward() {
        leftMotor.setSpeed( Constants.VEHICLE_FORWARD_SPEED_LOW );
        leftMotor.forward();
    }

    /**
     * A method to rotate the right motor forward
     */
    public void rotateRightMotorForward() {
        rightMotor.setSpeed( Constants.VEHICLE_FORWARD_SPEED_LOW );
        rightMotor.forward();
    }

    /**
     * A method to rotate our vehicle counter-clockwise
     */
    public void rotateCounterClockwise() {
        leftMotor.setSpeed( -Constants.VEHICLE_ROTATE_SPEED );
        rightMotor.setSpeed( Constants.VEHICLE_ROTATE_SPEED );
        leftMotor.backward();
        rightMotor.forward();
    }

    /**
     * A method to stopMotors our motors
     */
    public void stopMotors() {
        leftMotor.stop(true);
        rightMotor.stop(false);
    }

    /**
     * Calculates the minimum angle to turn to.
     *
     * @param deltaX
     * @param deltaY
     *
     * @return double minAngle
     */
    public double calculateMinAngle( double deltaX , double deltaY ) {
        // calculate the minimum angle
        double theta =  Math.atan2( deltaX , deltaY ) - odometer.getTheta();
        if ( theta < -Math.PI ) {
            theta += ( 2*Math.PI );
        } else if ( theta > Math.PI ) {
            theta -= ( 2*Math.PI );
        }
        return theta;
    }

    /**
     * Calculates the distance to a specific point.
     *
     * @param deltaX
     * @param deltaY
     * @return
     */
    public double calculateDistanceToPoint( double deltaX , double deltaY ) {
        return Math.hypot( deltaX , deltaY );
    }


    /**
     * Determine the angle our motors need to rotate in order for vehicle to turn a certain angle.
     *
     * @param angle
     * @return
     */
    public int convertAngle( double angle ) {
        return convertDistance( Math.PI * Constants.TRACK_LENGTH * angle / 360.0 );
    }

    /**
     * Determine how much the motor must rotate for vehicle to reach a certain distance.
     *
     * @param distance
     * @return
     */
    public int convertDistance( double distance ) {
        return (int) ( (180.0 * distance) / (Math.PI * Constants.WHEEL_RADIUS) );
    }

    /**
     * A method to determine if the vehicle is within the threshold of a certain distance
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isAtPosition( double x, double y ) {
        if ( Math.abs( odometer.getX() - x ) > 5 ) {
            return false;
        }
        if ( Math.abs( odometer.getY() - y ) > 5 ) {
            return false;
        }
        return true;
    }

    /**
     * A method that returns the current x-coordinate at which our vehicle is in the process of travelling to
     *
     * @return
     */
    public double getTravellingToX() {
        return travellingToX;
    }

    /**
     * A method that returns the current y-coordinate at which our vehicle is in the process of travelling to
     *
     * @return
     */
    public double getTravellingToY() {
        return travellingToY;
    }
}
