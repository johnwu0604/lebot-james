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
    private ObstacleMapper obstacleMapper;

    // variables
    private boolean correctionNeeded = true;
    private boolean obstacleMappingNeeded = false;
    ArrayList<Square> recentMoves = new ArrayList<Square>();
    private int movesInCurrentDirection = 0;

    /**
     * Default constructor for Navigator object.
     *
     * @param leftMotor the left motor EV3 object used in the robot
     * @param rightMotor the right motor EV3 object used in the robot
     * @param odometer the odometer controller used in the robot
     */
    public Navigator( EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, Odometer odometer,
                      ObstacleAvoider obstacleAvoider, ObstacleMapper obstacleMapper ) {
        this.odometer = odometer;
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.obstacleAvoider = obstacleAvoider;
        this.obstacleMapper = obstacleMapper;
        obstacleAvoider.setNavigator( this );
    }

    /**
     * A method to travel to a certain square, calls recursive greedy algorithm to move
     *
     * @param destination
     */
    public void travelToSquare( Square destination ) {
        recentMoves.clear();
        if(destination == odometer.getLastSquare()){
            Square lastSquare = odometer.getLastSquare();
            int[] components = getComponentDistances(lastSquare);

            if(components[0] != 0){
                moveSquareX(components[0]);
            } else {
                moveSquareY(components[1]);
            }

        } else {
            while (destination != odometer.getCurrentSquare()) {   //check break condition
                makeBestMoves(destination);
            }
        }
    }


    /**
     * A method to recursively execute the best allowed move until destination is reached
     *
     * @param destination
     */
    public void makeBestMoves(Square destination){

        Stack<Square> possibleMoves = getPossibleMoves(destination);
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
    public Stack<Square> getPossibleMoves(Square destination){

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
            if( deltaY != 0 ){
                secondPriority = deltaY > 0 ? northSquare : southSquare;
            }
        } else { // greater distance to travel in y
            topPriority = deltaY > 0 ? northSquare : southSquare;
            if( deltaX != 0 ) {
                secondPriority = deltaX > 0 ? eastSquare : westSquare;
            }
        }

        if( recentMoves.contains( odometer.getCurrentSquare() ) ){ //check to prevent moving in a loop

            fourthPriority = recentMoves.get( recentMoves.indexOf( odometer.getCurrentSquare() ) + 1 ); //Don't make same move as last time
            thirdPriority = odometer.getLastSquare();

            if (secondPriority == null) {

                if ( northSquare != topPriority && northSquare != thirdPriority && northSquare != fourthPriority ) {
                    secondPriority = northSquare;
                } else if ( southSquare != topPriority && southSquare != thirdPriority && southSquare != fourthPriority ) {
                    secondPriority = southSquare;
                } else if ( eastSquare != topPriority && eastSquare != thirdPriority && eastSquare != fourthPriority ) {
                    secondPriority = eastSquare;
                } else if ( westSquare != topPriority && westSquare != thirdPriority && westSquare != fourthPriority ) {
                    secondPriority = westSquare;
                }
            }

            if ( fourthPriority != null) {
                possibleMoves.push( fourthPriority );
            }
            if ( thirdPriority != null && thirdPriority != fourthPriority ) {
                possibleMoves.push( thirdPriority );
            }
            if ( secondPriority != null && secondPriority != fourthPriority) {
                possibleMoves.push( secondPriority );
            }
            if ( topPriority != null && topPriority != fourthPriority) {
                possibleMoves.push( topPriority );
            }

            return possibleMoves;

        } else {
            fourthPriority = odometer.getLastSquare();
        }

        ArrayList<Square> thirdPriorities = new ArrayList<Square>();

        // set last square remaining as third priority
        if ( northSquare != topPriority && northSquare != secondPriority && northSquare != fourthPriority ){
            thirdPriorities.add( northSquare );
        } else if ( southSquare != topPriority && southSquare != secondPriority && southSquare != fourthPriority ){
            thirdPriorities.add( southSquare );
        } else if ( eastSquare != topPriority && eastSquare != secondPriority && eastSquare != fourthPriority ){
            thirdPriorities.add( eastSquare );
        } else if ( westSquare != topPriority && westSquare != secondPriority && westSquare != fourthPriority ){
            thirdPriorities.add( westSquare );
        }

        if(thirdPriorities.size() == 1) {
            thirdPriority = thirdPriorities.get(0);
        }else {

            double distOne = Math.hypot((double) (thirdPriorities.get(0).getSquarePosition()[0] - destination.getSquarePosition()[0]),
                    (double) (thirdPriorities.get(0).getSquarePosition()[1] - destination.getSquarePosition()[1]));
            double distTwo = Math.hypot((double) (thirdPriorities.get(1).getSquarePosition()[0] - destination.getSquarePosition()[0]),
                    (double) (thirdPriorities.get(1).getSquarePosition()[1] - destination.getSquarePosition()[1]));

            if (distOne < distTwo) { //Opt for square TOWARDS destination
                secondPriority = thirdPriorities.get(0);
                thirdPriority = thirdPriorities.get(1);
            } else if (distTwo < distOne) {
                secondPriority = thirdPriorities.get(1);
                thirdPriority = thirdPriorities.get(0);
            } else if (deltaX == 0) { //If both possible moves equidistant
                //obstacle directly in path, move AROUND, not AWAY, i.e. parallel to obstacle
                if (thirdPriorities.get(0).getSquarePosition()[0] == odometer.getCurrentSquare().getSquarePosition()[0]) {
                    secondPriority = thirdPriorities.get(1); //Y-direction move
                    thirdPriority = thirdPriorities.get(0); //X-direction move
                } else if (thirdPriorities.get(1).getSquarePosition()[0] == odometer.getCurrentSquare().getSquarePosition()[0]) {
                    secondPriority = thirdPriorities.get(0); //X-direction move
                    thirdPriority = thirdPriorities.get(1); //Y-direction move
                }
            } else if (deltaY == 0) {
                //obstacle directly in path, move AROUND, not AWAY, i.e. parallel to obstacle
                if (thirdPriorities.get(0).getSquarePosition()[1] == odometer.getCurrentSquare().getSquarePosition()[1]) {
                    secondPriority = thirdPriorities.get(1);
                    thirdPriority = thirdPriorities.get(0);
                } else if (thirdPriorities.get(1).getSquarePosition()[1] == odometer.getCurrentSquare().getSquarePosition()[1]) {
                    secondPriority = thirdPriorities.get(0);
                    thirdPriority = thirdPriorities.get(1);
                }
            } else { //maintain current heading before choosing to turn
                if (getNextSquare() == thirdPriorities.get(0)) {
                    secondPriority = thirdPriorities.get(0);
                    thirdPriority = thirdPriorities.get(1);
                } else if (getNextSquare() == thirdPriorities.get(1)) {
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
     * A method to travel to a specific x coordinate
     *
     * @param xCoordinate the x coordinate we want to travel to
     */
    public void travelToX( double xCoordinate ) {
        turnRobot( calculateMinAngle( xCoordinate - odometer.getX(), 0 ) );
        turnOnNecessaryThreads();
        driveForward();
        while ( Math.abs( odometer.getX() - xCoordinate ) > ThresholdConstants.POINT_REACHED) {
            if ( odometer.isCorrecting() ) {
                waitUntilCorrectionIsFinished();
            }
        }
        stopMotors();
        turnOffNecessaryThreads();
    }

    /**
     * A method to travel to a specific y coordinate
     *
     * @param yCoordinate the y coordinate we want to travel to
     */
    public void travelToY( double yCoordinate ) {
        turnRobot( calculateMinAngle( 0, yCoordinate - odometer.getY() ) );
        turnOnNecessaryThreads();
        driveForward();
        while ( Math.abs( odometer.getY() - yCoordinate ) > ThresholdConstants.POINT_REACHED) {
            if ( odometer.isCorrecting() ) {
                waitUntilCorrectionIsFinished();
            }
        }
        stopMotors();
        turnOffNecessaryThreads();
    }

    /**
     * A method to travel to a specific x coordinate backwards (doesn't use odometry correction)
     *
     * @param xCoordinate the x coordinate we want to travel to
     */
    public void travelToXBackward( double xCoordinate ) {
        double minAngle = calculateMinAngle( xCoordinate - odometer.getX(), 0 );
        minAngle += minAngle < 0 ? Math.PI : - Math.PI;
        // turnRobot to the minimum angle
        turnRobot( minAngle );
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
        // turnRobot to the minimum angle
        turnRobot( minAngle );
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
            if ( isMovePossible( destinationSquare ) ) {
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
            if ( isMovePossible( destinationSquare ) ) {
                travelToY( destinationSquare.getCenterCoordinate()[1] );
                return true;
            }
        }

        return false;
    }

    /**
     * A method to determine whether a move is possible by scanning the square if needed
     *
     * @param destination
     * @return isMovePossible
     */
    public boolean isMovePossible( Square destination ) {
        // if move has been made, then we can make the move again
        if ( odometer.getPastSquares().contains( destination ) ) {
            return true;
        }
        // if mapping is needed or we are at the edge squares then a scan is needed
        if ( obstacleMappingNeeded || odometer.getFieldMapper().isEdgeSquare( destination ) ) {
            if ( !obstacleAvoider.scanSquare( destination ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * A method to determine if the square we want to move to is allowed or not
     *
     * @param x
     * @param y
     * @return isSquareAllowed
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
     * A method to turnRobot our vehicle to a certain angle in either direction
     *
     * @param theta the theta angle that we want to turnRobot our vehicle
     */
    public void turnRobot(double theta ) {
        leftMotor.setSpeed( NavigationConstants.VEHICLE_ROTATE_SPEED );
        rightMotor.setSpeed( NavigationConstants.VEHICLE_ROTATE_SPEED );
        if( theta < 0 ) { // if angle is negative, turnRobot to the left
            leftMotor.rotate( -convertAngle( -(theta*180)/Math.PI ) , true );
            rightMotor.rotate( convertAngle( -(theta*180)/Math.PI ) , false );
        }
        else { // angle is positive, turnRobot to the right
            leftMotor.rotate( convertAngle( (theta*180)/Math.PI ) , true);
            rightMotor.rotate( -convertAngle( (theta*180)/Math.PI ) , false);
        }
    }

    /**
     * A method to rotate the left motor forward slowly
     */
    public void rotateLeftMotorForwardSlow() {
        leftMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION_SLOW );
        leftMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED_SLOW);
        leftMotor.forward();
    }

    /**
     * A method to rotate the right motor forward slowly
     */
    public void rotateRightMotorForwardSlow() {
        rightMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION_SLOW );
        rightMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED_SLOW);
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
     * A method to rotate the left motor backward slowly
     */
    public void rotateLeftMotorBackwardSlow() {
        leftMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION_SLOW );
        leftMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED_SLOW);
        leftMotor.backward();
    }

    /**
     * A method to rotate the right motor backward slowly
     */
    public void rotateRightMotorBackwardSlow() {
        rightMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION_SLOW );
        rightMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED_SLOW);
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
     * A method to drive the vehicle forward slowly
     */
    public void driveForwardSlow() {
        leftMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION_SLOW );
        rightMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION_SLOW );
        leftMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED_SLOW);
        rightMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED_SLOW);
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
     * Calculates the minimum angle to turnRobot to.
     *
     * @param deltaX the x coordinate of our destination
     * @param deltaY the y coordinate of our destination
     *
     * @return the minimum angle we need to turnRobot
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
     * Determine the angle our motors need to rotate in order for vehicle to turnRobot a certain angle.
     *
     * @param angle the angle we want to turnRobot
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

    /**
     * A method to stop both motors
     */
    public void stop(){
        leftMotor.stop(true);
        rightMotor.stop(false);
    }

    /**
     * A method to stop both motors
     */
    public void stopFast(){
        leftMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION_FAST );
        rightMotor.setAcceleration( NavigationConstants.VEHICLE_ACCELERATION_FAST );
        leftMotor.stop(true);
        rightMotor.stop(false);
    }

    /**
     * A method to return the components distances to a destination in square units
     *
     * @param destination
     * @return componentDistances in square units
     */
    public int[] getComponentDistances( Square destination ){
        int components[] = new int[2];
        components[0] = destination.getSquarePosition()[0] - odometer.getCurrentSquare().getSquarePosition()[0];
        components[1] = destination.getSquarePosition()[1] - odometer.getCurrentSquare().getSquarePosition()[1];
        return components;
    }

    /**
     * A method to turnRobot towards the center of a specified square
     *
     * @param square
     */
    public void turnToSquare( Square square ) {
        double deltaX = square.getCenterCoordinate()[0] - odometer.getX();
        double deltaY = square.getCenterCoordinate()[1] - odometer.getY();
        turnRobot( calculateMinAngle( deltaX, deltaY ) );
    }

    /**
     * A method to travel directly a certain distance
     *
     * @param distance
     */
    public void moveDistance( double distance ) {
        // drive forward two tiles
        leftMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED_SLOW );
        rightMotor.setSpeed( NavigationConstants.VEHICLE_FORWARD_SPEED_SLOW );
        leftMotor.rotate( convertDistance( distance ), true);
        rightMotor.rotate( convertDistance( distance ), false);
    }

    /**
     * A method to set whether obstacle mapping is needed or not
     *
     * @param obstacleMappingNeeded
     */
    public void setObstacleMappingNeeded( boolean obstacleMappingNeeded ) {
        this.obstacleMappingNeeded = obstacleMappingNeeded;
    }

    /**
     * A method to turn on necessary threads when making navigation moves
     */
    public void turnOnNecessaryThreads() {
        if ( correctionNeeded ) {
            odometerCorrection.startRunning();
        }
        if ( obstacleMappingNeeded ) {
            obstacleMapper.startRunning();
        }
    }

    /**
     * A method to turn off necessary threads after making navigation moves
     */
    public void turnOffNecessaryThreads() {
        if ( correctionNeeded ) {
            odometerCorrection.stopRunning();
        }
        if ( obstacleMappingNeeded ) {
            obstacleMapper.stopRunning();
        }
    }


}