package main.controller;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import main.object.Square;
import main.resource.ThresholdConstants;
import main.resource.NavigationConstants;
import main.resource.RobotConstants;
import java.util.Stack;
import java.util.ArrayList;


/**
 * Navigator object used to navigate the vehicle.
 *
 * @author JohnWu
 */
public class Navigator {

    // objects
    private Odometer odometer;
    private EV3LargeRegulatedMotor leftMotor, rightMotor;
    private OdometerCorrection odometerCorrection;
    private ObstacleAvoider obstacleAvoider;

    private boolean correctionNeeded = true;

    /**
     * Default constructor for Navigator object.
     *
     * @param leftMotor the left motor EV3 object used in the robot
     * @param rightMotor the right motor EV3 object used in the robot
     * @param odometer the odometer controller used in the robot
     */
    public Navigator( EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Odometer odometer,
                      ObstacleAvoider obstacleAvoider ) {
        this.odometer = odometer;
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.obstacleAvoider = obstacleAvoider;
        obstacleAvoider.setNavigator( this );
    }

    /**
     * A method to travel to a certain square, calls recursive greedy algorithm to move
     *
     * @param square
     */
    public void travelToSquare( Square square ) {
        if(square == odometer.getLastSquare()){
            Square lastSquare = odometer.getLastSquare();
            int[] components = getComponentDistances(lastSquare);

            if(components[0] != 0){
                moveSquareX(components[0]);
            } else {
                moveSquareY(components[1]);
            }

        } else {

            while (square != odometer.getCurrentSquare()) {   //check break condition
                makeBestMoves(square);
            }
        }
    }


    /**
     * A method to recursively execute the best allowed move until destination is reached
     * @param destination
     */
    public void makeBestMoves(Square destination){

        Stack<Square> possibleMoves = getPossibleMoves( destination );
        boolean moveCompleted = false;

        while( !possibleMoves.empty() && !moveCompleted ){
            Square moveLocation = possibleMoves.pop();

            if (moveLocation == odometer.getNorthSquare()){
                moveCompleted = moveSquareY(1);
            } else if (moveLocation == odometer.getSouthSquare()){
                moveCompleted = moveSquareY(-1);
            } else if (moveLocation == odometer.getEastSquare()){
                moveCompleted = moveSquareX(1);
            } else if (moveLocation == odometer.getWestSquare()){
                moveCompleted = moveSquareX(-1);
            }
        }

    }

    /**
     * A method that returns the possible moves the robot can make, with priority
     * @param destination
     * @return stack of prioritized moves
     */
    public Stack<Square> getPossibleMoves(Square destination){

        Stack<Square> possibleMoves = new Stack<Square>();

        possibleMoves.push( odometer.getLastSquare() );

        Square topPriority;
        Square secondPriority = null;
        Square thirdPriority = null;

        Square northSquare = odometer.getNorthSquare();
        Square southSquare = odometer.getSouthSquare();
        Square eastSquare = odometer.getEastSquare();
        Square westSquare = odometer.getWestSquare();

        int deltaX = getComponentDistances(destination)[0]; // in square values
        int deltaY = getComponentDistances(destination)[1]; // in square values

        // greater distance to travel in x
        if ( Math.abs( deltaX ) > Math.abs( deltaY ) ) {
            topPriority = deltaX > 0 ? eastSquare : westSquare;
            if(deltaY != 0){
                secondPriority = deltaY > 0 ? northSquare : southSquare;
            }
        } else { // greater distance to travel in y
            topPriority = deltaY > 0 ? northSquare : southSquare;
            if(deltaX != 0) {
                secondPriority = deltaX > 0 ? eastSquare : westSquare;
            }
        }

        ArrayList<Square> thirdPriorities = new ArrayList<Square>();

        // set last square remaining as third priority
        if (northSquare != topPriority && northSquare != secondPriority && northSquare != odometer.getLastSquare()){
            thirdPriorities.add(northSquare);
        } else if (southSquare != topPriority && southSquare != secondPriority && southSquare != odometer.getLastSquare()){
            thirdPriorities.add(southSquare);
        } else if (eastSquare != topPriority && eastSquare != secondPriority && eastSquare != odometer.getLastSquare()){
            thirdPriorities.add(eastSquare);
        } else {
            thirdPriorities.add(westSquare);
        }

        if(thirdPriorities.size() == 1) {
            thirdPriority = thirdPriorities.get(0);
        }else if (thirdPriorities.size() == 2){

            double distOne = Math.hypot((double) thirdPriorities.get(0).getSquarePosition()[0], (double) thirdPriorities.get(0).getSquarePosition()[1]);
            double distTwo = Math.hypot((double) thirdPriorities.get(1).getSquarePosition()[0], (double) thirdPriorities.get(1).getSquarePosition()[1]);

            if(distOne <= distTwo){
                secondPriority = thirdPriorities.get(0);
                thirdPriority = thirdPriorities.get(1);
            } else {
                secondPriority = thirdPriorities.get(1);
                thirdPriority = thirdPriorities.get(0);
            }
        }

        // third priority might be a wall (null)
        if ( thirdPriority != null && thirdPriority != odometer.getLastSquare() ) {
            possibleMoves.push(thirdPriority);
        }
        if ( secondPriority != null && secondPriority != odometer.getLastSquare() ) {
            possibleMoves.push(secondPriority);
        }
        if ( topPriority != null && topPriority != odometer.getLastSquare() ) {
            possibleMoves.push(topPriority);
        }

        return possibleMoves;

    }

    /**
     * A method to drive our vehicle to a certain cartesian coordinate.
     *
     * @param x X-Coordinate
     * @param y Y-Coordinate
     */
    public void travelTo( double x , double y ) {
        travelToY(y);
        travelToX(x);
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
        if ( correctionNeeded ) {
            odometerCorrection.startRunning();
        }
        driveForward();
        while ( Math.abs( odometer.getX() - xCoordinate ) > ThresholdConstants.POINT_REACHED) {
            if ( odometer.isCorrecting() ) {
                waitUntilCorrectionIsFinished();
            }
        }
        stopMotors();
        if ( correctionNeeded ) {
            odometerCorrection.stopRunning();
        }
    }

    /**
     * A method to travel to a specific y coordinate
     *
     * @param yCoordinate the y coordinate we want to travel to
     */
    public void travelToY( double yCoordinate ) {
        // turn to the minimum angle
        turnTo( calculateMinAngle( 0, yCoordinate - odometer.getY() ) );
        if ( correctionNeeded ) {
            odometerCorrection.startRunning();
        }
        // move to the specified point
        driveForward();
        while ( Math.abs( odometer.getY() - yCoordinate ) > ThresholdConstants.POINT_REACHED) {
            if ( odometer.isCorrecting() ) {
                waitUntilCorrectionIsFinished();
            }
        }
        stopMotors();
        if ( correctionNeeded ) {
            odometerCorrection.stopRunning();
        }
    }

    /**
     * A method to travel to a specific x coordinate backwards (doesn't use odometry correction)
     *
     * @param xCoordinate the x coordinate we want to travel to
     */
    public void travelToXBackward( double xCoordinate ) {
        double minAngle = calculateMinAngle( xCoordinate - odometer.getX(), 0 );
        minAngle += minAngle < 0 ? Math.PI : - Math.PI;
        // turn to the minimum angle
        turnTo( minAngle );
        // move to the specified point
        while ( Math.abs( odometer.getX() - xCoordinate ) > ThresholdConstants.POINT_REACHED) {
            driveBackward();
        }
        stopMotors();
    }

    /**
     * A method to travel to a specific y coordinate backwards (doesn't use odometry correction)
     *
     * @param yCoordinate the y coordinate we want to travel to
     */
    public void travelToYBackward( double yCoordinate ) {
        double minAngle = calculateMinAngle( 0, yCoordinate - odometer.getY() );
        minAngle += minAngle < 0 ? Math.PI : - Math.PI;
        // turn to the minimum angle
        turnTo( minAngle );
        // move to the specified point
        while ( Math.abs( odometer.getY() - yCoordinate ) > ThresholdConstants.POINT_REACHED) {
            driveBackward();
        }
        stopMotors();
    }

    /**
     * A method to move the robot 1 square in the x-direction
     *
     * @param direction
     * @return if move was made or not
     */
    public boolean moveSquareX( int direction ){

        int currentX = odometer.getCurrentSquare().getSquarePosition()[0];
        int currentY = odometer.getCurrentSquare().getSquarePosition()[1];

        int xDestination = currentX;
        xDestination += direction > 0 ? 1 : -1;

        Square destinationSquare = odometer.getFieldMapper().getMapping()[xDestination][currentY];

        if( isSquareAllowed( xDestination, currentY ) ){
            if(odometer.getPastSquares().contains(destinationSquare)){
                travelToX( destinationSquare.getCenterCoordinate()[0] );
                return true;
            } else if ( obstacleAvoider.scanSquare( destinationSquare )) {
                travelToX( destinationSquare.getCenterCoordinate()[0] );
                return true;
            }
        }
        return false;

    }

    /**
     * A method to move the robot one square in the y-direction
     *
     * @param direction
     * @return if move was made or not
     */
    public boolean moveSquareY(int direction){

        int currentX = odometer.getCurrentSquare().getSquarePosition()[0];
        int currentY = odometer.getCurrentSquare().getSquarePosition()[1];

        int yDestination = currentY;
        yDestination += direction > 0 ? 1 : -1;

        Square destinationSquare = odometer.getFieldMapper().getMapping()[currentX][yDestination];

        if( isSquareAllowed( currentX, yDestination ) ){
            if(odometer.getPastSquares().contains(destinationSquare)){
                travelToY( destinationSquare.getCenterCoordinate()[1] );
                return true;
            }else if ( obstacleAvoider.scanSquare( destinationSquare ) ) {
                travelToY( destinationSquare.getCenterCoordinate()[1] );
                return true;
            }
        }
        return false;
    }

    /**
     * A method to determine if the square we want to move to is allowed or not
     *
     * @param x
     * @param y
     * @return is the square allowed
     */
    public boolean isSquareAllowed( int x, int y ) {
        return odometer.getFieldMapper().getMapping()[x][y].isAllowed();
    }


    /**
     * A method to turn our vehicle to a certain angle in either direction
     *
     * @param theta the theta angle that we want to turn our vehicle
     */
    public void turnTo( double theta ) {
        leftMotor.setSpeed( NavigationConstants.VEHICLE_ROTATE_SPEED );
        rightMotor.setSpeed( NavigationConstants.VEHICLE_ROTATE_SPEED );
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
        leftMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION );
        leftMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED);
        leftMotor.forward();
    }

    /**
     * A method to rotate the right motor forward
     */
    public void rotateRightMotorForward() {
        rightMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION );
        rightMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED);
        rightMotor.forward();
    }

    /**
     * A method to rotate the left motor backward
     */
    public void rotateLeftMotorBackward() {
        leftMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION );
        leftMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED);
        leftMotor.backward();
    }

    /**
     * A method to rotate the right motor backward
     */
    public void rotateRightMotorBackward() {
        rightMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION );
        rightMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED);
        rightMotor.backward();
    }

    /**
     * A method to drive the vehicle forward
     */
    public void driveForward() {
        leftMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION );
        rightMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION );
        leftMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED);
        rightMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED);
        leftMotor.forward();
        rightMotor.forward();
    }

    /**
     * A method to drive the vehicle backward
     */
    public void driveBackward() {
        leftMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION );
        rightMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION );
        leftMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED);
        rightMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED);
        leftMotor.backward();
        rightMotor.backward();
    }

    /**
     * A method to rotate our vehicle counter-clockwise
     */
    public void rotateCounterClockwise() {
        leftMotor.setSpeed( -NavigationConstants.VEHICLE_ROTATE_SPEED );
        rightMotor.setSpeed( NavigationConstants.VEHICLE_ROTATE_SPEED );
        leftMotor.backward();
        rightMotor.forward();
    }

    /**
     * A method to rotate our vehicle clockwise
     */
    public void rotateClockwise() {
        leftMotor.setSpeed( NavigationConstants.VEHICLE_ROTATE_SPEED );
        rightMotor.setSpeed( -NavigationConstants.VEHICLE_ROTATE_SPEED );
        leftMotor.forward();
        rightMotor.backward();
    }

    /**
     * A method to rotate our vehicle counter-clockwise for localization
     */
    public void rotateCounterClockwiseLocalization() {
        leftMotor.setSpeed( -NavigationConstants.LOCALIZATION_ROTATE_SPEED );
        rightMotor.setSpeed( NavigationConstants.LOCALIZATION_ROTATE_SPEED );
        leftMotor.backward();
        rightMotor.forward();
    }

    /**
     * A method to rotate our vehicle counter-clockwise for localization fast
     */
    public void rotateCounterClockwiseLocalizationFast() {
        leftMotor.setSpeed( -NavigationConstants.LOCALIZATION_ROTATE_SPEED_FAST );
        rightMotor.setSpeed( NavigationConstants.LOCALIZATION_ROTATE_SPEED_FAST );
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
    }

    /**
     * A method to stop our right motor
     */
    public void stopRightMotor() {
        rightMotor.stop(true);
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
        return convertDistance( Math.PI * RobotConstants.TRACK_LENGTH * angle / 360.0 );
    }

    /**
     * Determine how much the motor must rotate for vehicle to reach a certain distance.
     *
     * @param distance the distance we want to travel
     * @return the tacho count that we need to rotate
     */
    public int convertDistance( double distance ) {
        return (int) ( (180.0 * distance) / (Math.PI * RobotConstants.WHEEL_RADIUS) );
    }

    /**
     * A method which waits until odometry correction finishes
     */
    public void waitUntilCorrectionIsFinished() {
        while ( odometer.isCorrecting() ) {
            try { Thread.sleep( 10 ); } catch( Exception e ){}
        }
    }

    /**
     * A method that returns whether correction is needed for the current movement
     *
     * @return
     */
    public boolean isCorrectionNeeded() {
        return correctionNeeded;
    }

    /**
     * A method to declare that correction is not needed for the current movement
     *
     * @param correctionNeeded
     */
    public void setCorrectionNeeded( boolean correctionNeeded ) {
        this.correctionNeeded = correctionNeeded;
    }

    public void setOdometerCorrection(OdometerCorrection odometerCorrection ) {
        this.odometerCorrection = odometerCorrection;
    }

    public Odometer getOdometer(){
        return this.odometer;
    }

    public void stop(){
        leftMotor.stop(true);
        rightMotor.stop(false);
    }

    public int[] getComponentDistances(Square destination){

        int components[] = new int[2];

        components[0] = destination.getSquarePosition()[0] - odometer.getCurrentSquare().getSquarePosition()[0];
        components[1] = destination.getSquarePosition()[1] - odometer.getCurrentSquare().getSquarePosition()[1];

        return components;
    }


}