package main.controller;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import main.object.Square;
import main.resource.Constants;
import main.object.UltrasonicSensor;


/**
 * Navigator object used to navigate the vehicle.
 *
 * @author JohnWu
 */
public class Navigator {

    // objects
    private Odometer odometer;
    private EV3LargeRegulatedMotor leftMotor, rightMotor;

    /**
     * Default constructor for Navigator object.
     *
     * @param leftMotor the left motor EV3 object used in the robot
     * @param rightMotor the right motor EV3 object used in the robot
     * @param odometer the odometer controller used in the robot
     */
    public Navigator( EV3LargeRegulatedMotor leftMotor , EV3LargeRegulatedMotor rightMotor , Odometer odometer) {
        this.odometer = odometer;
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
    }

    /**
     * A method to travel to a certain square.
     *
     * @param square
     */
    public void travelToSquare( Square square ) {

        int deltaX = square.getSquarePosition()[0] - odometer.getCurrentSquare().getSquarePosition()[1];
        int deltaY = square.getSquarePosition()[1] - odometer.getCurrentSquare().getSquarePosition()[1];

        while (deltaX != 0 || deltaY != 0){

            if(deltaX >= deltaY){
                moveSquareX(deltaX);
                deltaX = square.getSquarePosition()[0] - odometer.getCurrentSquare().getSquarePosition()[1];
            }else{
                moveSquareY(deltaY);
                deltaY = square.getSquarePosition()[1] - odometer.getCurrentSquare().getSquarePosition()[1];
            }

        }
    }

    /**
     * A method to drive our vehicle to a certain cartesian coordinate.
     *
     * @param x X-Coordinate
     * @param y Y-Coordinate
     */
    public void travelTo( double x , double y ) {
        travelToX(x);
        travelToY(y);
    }

    /**
     * A method to travel to a specific x coordinate
     *
     * @param xCoordinate the x coordinate we want to travel to
     */
    public void travelToX( double xCoordinate ) {
        // turn to the minimum angle
        turnTo( calculateMinAngle( xCoordinate - odometer.getX(), 0 ) );
        // move to the specified point
        driveForward();
        while ( Math.abs( odometer.getX() - xCoordinate ) > Constants.POINT_REACHED_THRESHOLD ) {
            if ( odometer.isCorrecting() ) {
                waitUntilCorrectionIsFinished();
                driveForward();
            }
        }
        stopMotors();
    }

    /**
     * A method to travel to a specific y coordinate
     *
     * @param yCoordinate the y coordinate we want to travel to
     */
    public void travelToY( double yCoordinate ) {
        // turn to the minimum angle
        turnTo( calculateMinAngle( 0, yCoordinate - odometer.getY() ) );
        // move to the specified point
        driveForward();
        while ( Math.abs( odometer.getY() - yCoordinate ) > Constants.POINT_REACHED_THRESHOLD ) {
            if ( odometer.isCorrecting() ) {
                waitUntilCorrectionIsFinished();
                driveForward();
            }
        }
        stopMotors();
    }

    /**
     * A method to move the robot 1 square in the x-direction
     *
     * @param direction
     */
    public void moveSquareX( int direction ){

        int currentX = odometer.getCurrentSquare().getSquarePosition()[0];
        int currentY = odometer.getCurrentSquare().getSquarePosition()[1];

        int xDest = currentX;

        if (direction > 0){
            xDest += 1;
        } else {
            xDest -= 1;
        }

        boolean moveAllowed = odometer.getFieldMapper().getMapping()[xDest][currentY].isAllowed();

        if(moveAllowed){
            double xCoordinate = odometer.getFieldMapper().getMapping()[xDest][currentY].getCenterCoordinate()[0];
            travelToX(xCoordinate);
        }

    }

    /**
     * A method to move the robot one square in the y-direction
     *
     * @param direction
     */
    public void moveSquareY(int direction){

        int currentX = odometer.getCurrentSquare().getSquarePosition()[0];
        int currentY = odometer.getCurrentSquare().getSquarePosition()[1];

        int yDest = currentY;
        if (direction > 0){
            yDest += 1;
        } else {
            yDest -= 1;
        }

        boolean moveAllowed = odometer.getFieldMapper().getMapping()[currentX][yDest].isAllowed();

        if(moveAllowed){
            double yCoorindate = odometer.getFieldMapper().getMapping()[currentX][yDest].getCenterCoordinate()[1];

            travelToY(yCoorindate);
        }

    }

    /**
     * A method to turn our vehicle to a certain angle in either direction
     *
     * @param theta the theta angle that we want to turn our vehicle
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
     * A method to drive the vehicle forward
     */
    public void driveForward() {
        leftMotor.setAcceleration( Constants.VEHICLE_ACCELERATION );
        rightMotor.setAcceleration( Constants.VEHICLE_ACCELERATION );
        leftMotor.setSpeed( Constants.VEHICLE_ROTATE_SPEED );
        rightMotor.setSpeed( Constants.VEHICLE_ROTATE_SPEED );
        leftMotor.forward();
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
     * A method to our motors
     */
    public void stopMotors() {
        leftMotor.stop(true);
        rightMotor.stop(false);
    }

    /**
     * A method to stop our left motor
     */
    public void stopLeftMotor() {
        leftMotor.stop(true);
        rightMotor.stop(false);
    }

    /**
     * A method to stop our right motor
     */
    public void stopRightMotor() {
        leftMotor.stop(true);
        rightMotor.stop(false);
    }


    /**
     * Calculates the minimum angle to turn to.
     *
     * @param deltaX the x coordinate of our destination
     * @param deltaY the y coordinate of our destination
     *
     * @return the minimum angle we need to turn
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
     * @param deltaX the x coordinate of our destination
     * @param deltaY the y coordinate of out destination
     * @return the distance to the point
     */
    public double calculateDistanceToPoint( double deltaX , double deltaY ) {
        return Math.hypot( deltaX , deltaY );
    }


    /**
     * Determine the angle our motors need to rotate in order for vehicle to turn a certain angle.
     *
     * @param angle the angle we want to turn
     * @return the tacho count that we need to rotate
     */
    public int convertAngle( double angle ) {
        return convertDistance( Math.PI * Constants.TRACK_LENGTH * angle / 360.0 );
    }

    /**
     * Determine how much the motor must rotate for vehicle to reach a certain distance.
     *
     * @param distance the distance we want to travel
     * @return the tacho count that we need to rotate
     */
    public int convertDistance( double distance ) {
        return (int) ( (180.0 * distance) / (Math.PI * Constants.WHEEL_RADIUS) );
    }

    /**
     * A method which waits until odometry correction finishes
     */
    public void waitUntilCorrectionIsFinished() {
        stopMotors();
        while ( odometer.isCorrecting() ) {
            // do nothing
        }
    }


}
