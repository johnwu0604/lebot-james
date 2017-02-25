package controller;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import resource.Constants;

/**
 * Navigator object used to navigate the vehicle.
 *
 * @author JohnWu
 */
public class Navigator {

    // objects
    private Odometer odometer;
    private EV3LargeRegulatedMotor leftMotor, rightMotor;

    // constants
    private double WHEEL_RADIUS, TRACK_LENGTH;
    private int FORWARD_SPEED, ROTATE_SPEED;

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
        WHEEL_RADIUS = Constants.WHEEL_RADIUS;
        TRACK_LENGTH = Constants.TRACK_LENGTH;
        FORWARD_SPEED = Constants.VEHICLE_FORWARD_SPEED_HIGH;
        ROTATE_SPEED = Constants.VEHICLE_ROTATE_SPEED;
    }

    /**
     * A method to drive our vehicle to a certain cartesian coordinate.
     *
     * @param x X-Coordinate
     * @param y Y-Coordinate
     */
    public void travelTo( double x , double y ) {
        double deltaX = x - odometer.getX();
        double deltaY = y - odometer.getY();

        // turn to the minimum angle
        turnTo( calculateMinAngle( deltaX , deltaY ) );

        // move to the specified point
        leftMotor.setSpeed( FORWARD_SPEED );
        rightMotor.setSpeed( FORWARD_SPEED );
        leftMotor.rotate( convertDistance( WHEEL_RADIUS , calculateDistanceToPoint( deltaX , deltaY ) ), true );
        rightMotor.rotate( convertDistance( WHEEL_RADIUS , calculateDistanceToPoint( deltaX , deltaY ) ), false );

        leftMotor.stop( true );
        rightMotor.stop( true );
    }

    /**
     * A method to turn our vehicle to a certain angle.
     *
     * @param theta
     */
    private void turnTo( double theta ) {
        leftMotor.setSpeed( ROTATE_SPEED );
        rightMotor.setSpeed( ROTATE_SPEED );

        if( theta < 0 ) { // if angle is negative, turn to the left
            leftMotor.rotate( -convertAngle( WHEEL_RADIUS , TRACK_LENGTH , -(theta*180)/Math.PI ) , true );
            rightMotor.rotate( convertAngle( WHEEL_RADIUS , TRACK_LENGTH , -(theta*180)/Math.PI) , false );
        }
        else { // angle is positive, turn to the right
            leftMotor.rotate( convertAngle( WHEEL_RADIUS , TRACK_LENGTH , (theta*180)/Math.PI) , true);
            rightMotor.rotate( -convertAngle( WHEEL_RADIUS , TRACK_LENGTH , (theta*180)/Math.PI) , false);
        }
    }

    /**
     * Calculates the distance to a specific point.
     *
     * @param deltaX
     * @param deltaY
     * @return
     */
    private double calculateDistanceToPoint( double deltaX , double deltaY ) {
        return Math.hypot( deltaX , deltaY );
    }

    /**
     * Calculates the minimum angle to turn to.
     *
     * @param deltaX
     * @param deltaY
     *
     * @return double minAngle
     */
    private double calculateMinAngle( double deltaX , double deltaY ) {
        // calculate the minimum angle
        return Math.atan2( deltaX , deltaY ) - odometer.getTheta();
    }

    /**
     * Determine the angle our motors need to rotate in order for vehicle to turn a certain angle.
     *
     * @param radius
     * @param TRACK
     * @param angle
     * @return
     */
    private static int convertAngle( double radius , double TRACK , double angle ) {
        return convertDistance( radius , Math.PI * TRACK * angle / 360.0 );
    }

    /**
     * Determine how much the motor must rotate for vehicle to reach a certain distance.
     *
     * @param radius
     * @param distance
     * @return
     */
    private static int convertDistance( double radius , double distance ) {
        return (int) ( (180.0 * distance) / (Math.PI * radius) );
    }
}
