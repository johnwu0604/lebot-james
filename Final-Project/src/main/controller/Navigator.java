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
     * @param destination
     */
    public void travelToSquare( Square destination ) {
        if(destination == odometer.getLastSquare()){
            Square lastSquare = odometer.getLastSquare();
            int[] components = getComponentDistances(lastSquare);

            if(components[0] != 0){
                moveSquareX(components[0]);
            } else {
                moveSquareY(components[1]);
            }

        } else {

            ArrayList<Square> recentMoves = new ArrayList<Square>();
            while (destination != odometer.getCurrentSquare()) {   //check break condition
                makeBestMoves(destination, recentMoves);
            }
        }
    }


    /**
     * A method to recursively execute the best allowed move until destination is reached
     * @param destination
     */
    public void makeBestMoves(Square destination, ArrayList<Square> recentMoves){

        Stack<Square> possibleMoves = getPossibleMoves(destination, recentMoves);
        boolean moveCompleted = false;

        while( !possibleMoves.empty() && !moveCompleted ){
            Square moveLocation = possibleMoves.pop();

            if (moveLocation == odometer.getNorthSquare()){
                moveCompleted = moveSquareY(1);
                if(moveCompleted){
                    recentMoves.add(odometer.getNorthSquare());
                }
            } else if (moveLocation == odometer.getSouthSquare()){
                moveCompleted = moveSquareY(-1);
                if(moveCompleted){
                    recentMoves.add(odometer.getSouthSquare());
                }
            } else if (moveLocation == odometer.getEastSquare()){
                moveCompleted = moveSquareX(1);
                if(moveCompleted){
                    recentMoves.add(odometer.getEastSquare());
                }
            } else if (moveLocation == odometer.getWestSquare()){
                moveCompleted = moveSquareX(-1);
                if(moveCompleted){
                    recentMoves.add(odometer.getWestSquare());
                }
            }
        }

    }

    /**
     * A method that returns the possible moves the robot can make, with priority
     * @param destination, recentMoves
     * @return stack of prioritized moves
     */
    public Stack<Square> getPossibleMoves(Square destination, ArrayList<Square> recentMoves){

        Stack<Square> possibleMoves = new Stack<Square>();

        Square northSquare = odometer.getNorthSquare();
        Square southSquare = odometer.getSouthSquare();
        Square eastSquare = odometer.getEastSquare();
        Square westSquare = odometer.getWestSquare();

        Square topPriority = null;
        Square secondPriority = null;
        Square thirdPriority = null;
        Square fourthPriority = null;

        int deltaX = getComponentDistances(destination)[0]; // in square values
        int deltaY = getComponentDistances(destination)[1]; // in square values

        // greater distance to travel in x
        if ( Math.abs( deltaX ) >= Math.abs( deltaY ) ) {
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

        if(recentMoves.contains(odometer.getCurrentSquare())){ //check to prevent moving in a loop

            fourthPriority = recentMoves.get(recentMoves.indexOf(odometer.getCurrentSquare())+1); //Don't make same move as last time
            thirdPriority = odometer.getLastSquare();

            if (secondPriority == null) {

                if (northSquare != topPriority && northSquare != thirdPriority && northSquare != fourthPriority) {
                    secondPriority = northSquare;
                } else if (southSquare != topPriority && southSquare != thirdPriority && southSquare != fourthPriority) {
                    secondPriority = southSquare;
                } else if (eastSquare != topPriority && eastSquare != thirdPriority && eastSquare != fourthPriority) {
                    secondPriority = eastSquare;
                } else if (westSquare != topPriority && westSquare != thirdPriority && westSquare != fourthPriority) {
                    secondPriority = westSquare;
                }
            }

                if ( fourthPriority != null) {
                    possibleMoves.push(fourthPriority);
                }
                if ( thirdPriority != null && thirdPriority != fourthPriority) {
                    possibleMoves.push(thirdPriority);
                }
                if ( secondPriority != null && secondPriority != fourthPriority) {
                    possibleMoves.push(secondPriority);
                }
                if ( topPriority != null && topPriority != fourthPriority) {
                    possibleMoves.push(topPriority);
                }

                return possibleMoves;

        } else {
            fourthPriority = odometer.getLastSquare();
        }

        ArrayList<Square> thirdPriorities = new ArrayList<Square>();

        // set last square remaining as third priority
        if (northSquare != topPriority && northSquare != secondPriority && northSquare != fourthPriority){
            thirdPriorities.add(northSquare);
        } else if (southSquare != topPriority && southSquare != secondPriority && southSquare != fourthPriority){
            thirdPriorities.add(southSquare);
        } else if (eastSquare != topPriority && eastSquare != secondPriority && eastSquare != fourthPriority){
            thirdPriorities.add(eastSquare);
        } else if (westSquare != topPriority && westSquare != secondPriority && westSquare != fourthPriority){
            thirdPriorities.add(westSquare);
        }

        if(thirdPriorities.size() == 1) {
            thirdPriority = thirdPriorities.get(0);
        }else{

            double distOne = Math.hypot((double) (thirdPriorities.get(0).getSquarePosition()[0]-destination.getSquarePosition()[0]),
                    (double) (thirdPriorities.get(0).getSquarePosition()[1]-destination.getSquarePosition()[1]));
            double distTwo = Math.hypot((double) (thirdPriorities.get(1).getSquarePosition()[0]-destination.getSquarePosition()[0]),
                    (double) (thirdPriorities.get(1).getSquarePosition()[1]-destination.getSquarePosition()[1]));

                if (distOne < distTwo){ //Opt for square TOWARDS destination
                    secondPriority = thirdPriorities.get(0);
                    thirdPriority = thirdPriorities.get(1);
                } else if (distTwo < distOne){
                    secondPriority = thirdPriorities.get(1);
                    thirdPriority = thirdPriorities.get(0);
                } else if(deltaX == 0){ //If both possible moves equidistant
                    //obstacle directly in path, move AROUND, not AWAY, i.e. parallel to obstacle
                        if (thirdPriorities.get(0).getSquarePosition()[0] == odometer.getCurrentSquare().getSquarePosition()[0]){
                           secondPriority = thirdPriorities.get(1); //Y-direction move
                           thirdPriority = thirdPriorities.get(0); //X-direction move
                        } else if (thirdPriorities.get(1).getSquarePosition()[0] == odometer.getCurrentSquare().getSquarePosition()[0]){
                            secondPriority = thirdPriorities.get(0); //X-direction move
                            thirdPriority = thirdPriorities.get(1); //Y-direction move
                        } else {
                            if(getNextSquare() == thirdPriorities.get(0)){ //maintain current heading before choosing to turn
                                secondPriority = thirdPriorities.get(0);
                                thirdPriority = thirdPriorities.get(1);
                            } else if (getNextSquare() == thirdPriorities.get(1)){
                                secondPriority = thirdPriorities.get(1);
                                thirdPriority = thirdPriorities.get(0);
                            } else { //Arbitrary choice, prefers to move in +/- Y-direction
                                secondPriority = thirdPriorities.get(0);
                                thirdPriority = thirdPriorities.get(1);
                            }
                        }
                } else if (deltaY == 0) {
                     //obstacle directly in path, move AROUND, not AWAY, i.e. parallel to obstacle
                        if (thirdPriorities.get(0).getSquarePosition()[1] == odometer.getCurrentSquare().getSquarePosition()[1]){
                            secondPriority = thirdPriorities.get(1);
                            thirdPriority = thirdPriorities.get(0);
                        } else if (thirdPriorities.get(1).getSquarePosition()[1] == odometer.getCurrentSquare().getSquarePosition()[1]){
                            secondPriority = thirdPriorities.get(0);
                            thirdPriority = thirdPriorities.get(1);
                        } else {
                            if(getNextSquare() == thirdPriorities.get(0)){ //maintain current heading before choosing to turn
                                secondPriority = thirdPriorities.get(0);
                                thirdPriority = thirdPriorities.get(1);
                            } else if (getNextSquare() == thirdPriorities.get(1)){
                                secondPriority = thirdPriorities.get(1);
                                thirdPriority = thirdPriorities.get(0);
                            } else { //Arbitrary choice, prefers to move in +/- Y-direction
                                secondPriority = thirdPriorities.get(0);
                                thirdPriority = thirdPriorities.get(1);
                            }
                        }
                } else { //maintain current heading before choosing to turn
                    if(getNextSquare() == thirdPriorities.get(0)){
                        secondPriority = thirdPriorities.get(0);
                        thirdPriority = thirdPriorities.get(1);
                    } else if (getNextSquare() == thirdPriorities.get(1)){
                        secondPriority = thirdPriorities.get(1);
                        thirdPriority = thirdPriorities.get(0);
                    } else { //Arbitrary choice, prefers to move in +/- Y-direction
                        secondPriority = thirdPriorities.get(0);
                        thirdPriority = thirdPriorities.get(1);
                    }
                }
        }

        if ( fourthPriority != null) {
            possibleMoves.push(fourthPriority);
        }
        if ( thirdPriority != null && thirdPriority != fourthPriority) {
            possibleMoves.push(thirdPriority);
        }
        if ( secondPriority != null && secondPriority != fourthPriority) {
            possibleMoves.push(secondPriority);
        }
        if ( topPriority != null && topPriority != fourthPriority) {
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
        driveForward();
        while ( Math.abs( odometer.getX() - xCoordinate ) > ThresholdConstants.POINT_REACHED) {
            if ( odometer.isCorrecting() ) {
                waitUntilCorrectionIsFinished();
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
        while ( Math.abs( odometer.getY() - yCoordinate ) > ThresholdConstants.POINT_REACHED) {
            if ( odometer.isCorrecting() ) {
                waitUntilCorrectionIsFinished();
            }
        }
        stopMotors();
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
     * A method to return the next square the robot will enter, if it moves in out current heading
     *
     * @return the square "in front" of the robot
     */
    public Square getNextSquare(){
        if (odometer.getCurrentDirection().equals("north")){
            return odometer.getNorthSquare();
        }else if (odometer.getCurrentDirection().equals("south")){
            return odometer.getSouthSquare();
        }else if (odometer.getCurrentDirection().equals("east")){
            return odometer.getEastSquare();
        }else if (odometer.getCurrentDirection().equals("west")){
            return odometer.getWestSquare();
        }else{
            return null; //should never occur
        }
    }


    /**
     * A method to turn our vehicle to a certain angle in either direction
     *
     * @param theta the theta angle that we want to turn our vehicle
     */
    public void turnTo( double theta ) {
        odometerCorrection.stopRunning();
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
        odometerCorrection.startRunning();
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

    public void setOdometerCorrection( OdometerCorrection odometerCorrection ) {
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