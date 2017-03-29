package main.controller;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import main.object.Square;
import main.resource.ThresholdConstants;
import main.resource.RobotConstants;
import main.resource.TimeConstants;
import main.util.FieldMapper;

import java.util.ArrayList;
import java.util.concurrent.Exchanger;

/**
 * Odometer object used to keep track of vehicle position at all times.
 *
 * @author JohnWu
 */
public class Odometer extends Thread {

    // objects
    private EV3LargeRegulatedMotor leftMotor, rightMotor;
    private FieldMapper fieldMapper;
    private Object lock;

    // variables
    private double x, y, theta;
    private int currentLeftMotorTachoCount, currentRightMotorTachoCount,
            prevLeftMotorTachoCount, prevRightMotorTachoCount;
    private boolean correcting = false;
    private Square currentSquare;
    private ArrayList<Square> pastSquares;

    /**
     * Default constructor for an odometer object.
     *
     * @param leftMotor the left motor EV3 object used in the robot
     * @param rightMotor the right motor EV3 object used in the robot
     */
    public Odometer( EV3LargeRegulatedMotor leftMotor , EV3LargeRegulatedMotor rightMotor, FieldMapper fieldMapper ) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.fieldMapper = fieldMapper;
        pastSquares = new ArrayList<Square>();
        lock = new Object();
        x = 0.0;
        y = 0.0;
        theta = 0.0;
        currentLeftMotorTachoCount = 0;
        currentRightMotorTachoCount = 0;
        prevLeftMotorTachoCount = 0;
        prevRightMotorTachoCount = 0;
    }

    /**
     * Our main odometer thread.
     */
    public void run() {
        long updateStart, updateEnd;

        while ( true ) {
            updateStart = System.currentTimeMillis();

            // Get current tachometer values
            currentLeftMotorTachoCount = leftMotor.getTachoCount();
            currentRightMotorTachoCount = rightMotor.getTachoCount();

            // Use our change in rotation values to calculate displacement of each wheel
            double leftMotorDisplacement =  calculateMotorDisplacement( currentLeftMotorTachoCount , prevLeftMotorTachoCount );
            double rightMotorDisplacement = calculateMotorDisplacement( currentRightMotorTachoCount , prevRightMotorTachoCount );

            // change in angle of our vehicle
            double thetaChange = calculateThetaChange( leftMotorDisplacement , rightMotorDisplacement );
            // change in distance of our vehicle
            double displacement = calculateVehicleDisplacement( leftMotorDisplacement , rightMotorDisplacement );

            prevLeftMotorTachoCount = currentLeftMotorTachoCount;
            prevRightMotorTachoCount = currentRightMotorTachoCount;

            synchronized ( lock ) {
                // update odometer values
                theta += thetaChange;
                if ( theta < 0 ) {
                    theta += 2*Math.PI;
                }
                if ( theta > 2*Math.PI ) {
                    theta -= 2*Math.PI;
                }
                x +=  calculateXDisplacement(displacement,theta);
                y += calculateYDisplacement(displacement,theta);
            }

            // ensure that the odometer only runs once every period
            updateEnd = System.currentTimeMillis();
            if ( updateEnd - updateStart < TimeConstants.ODOMETER_PERIOD ) {
                try {
                    Thread.sleep( TimeConstants.ODOMETER_PERIOD - ( updateEnd - updateStart ) );
                } catch ( InterruptedException e ) {
                    // there is nothing to be done here because it is not
                    // expected that the odometer will be interrupted by
                    // another thread
                }
            }
        }
    }

    /**
     * Calculates the motor displacement based on current and previous tacho counts.
     *
     * @param currentTachoCount the current tacho count of the motor
     * @param prevTachoCount the previous tacho count of the motor
     * @return the displacement of the motor
     */
    public double calculateMotorDisplacement( int currentTachoCount , int prevTachoCount ) {
        int tachoDelta = currentTachoCount - prevTachoCount;
        return (2*Math.PI* RobotConstants.WHEEL_RADIUS)*tachoDelta/360;
    }

    /**
     * Calculates the change in theta based on the displacements of both motors.
     *
     * @param leftMotorDisplacement the displacement of the left motor
     * @param rightMotorDisplacement the displacement of the right motot
     * @return the theta change of the robot
     */
    public double calculateThetaChange( double leftMotorDisplacement , double rightMotorDisplacement ) {
        return ( leftMotorDisplacement - rightMotorDisplacement ) / RobotConstants.TRACK_LENGTH;
    }

    /**
     * Calculates the vehicle displacement based on the displacements of both motors.
     *
     * @param leftMotorDisplacement the displacement of the left motor
     * @param rightMotorDisplacement the displacement of the right motor
     * @return the total displacement of the vehicle
     */
    public double calculateVehicleDisplacement(double leftMotorDisplacement , double rightMotorDisplacement ) {
        return ( leftMotorDisplacement + rightMotorDisplacement ) / 2;
    }

    /**
     * Calculates the x-displacement of the vehicle.
     *
     * @param vehicleDisplacement the vehicle displacement
     * @param theta the theta change
     * @return the total displacement in the the x direction
     */
    public double calculateXDisplacement( double vehicleDisplacement , double theta ) {
        return vehicleDisplacement*Math.sin( theta );
    }

    /**
     * Calculates the y-displacement of the vehicle.
     *
     * @param vehicleDisplacement the vehicle displacement
     * @param theta the theta change
     * @return the total displacement in the y direction
     */
    public double calculateYDisplacement( double vehicleDisplacement , double theta ) {
        return vehicleDisplacement*Math.cos( theta );
    }

    /**
     * A method to get the x-coordinate of the our vehicle position.
     *
     * @return the x coordinate
     */
    public double getX() {
        double result;
        synchronized ( lock ) {
            result = x;
        }
        return result;
    }

    /**
     * Returns the list of the past squares we have travelled
     *
     * @return pastSquares
     */
    public ArrayList<Square> getPastSquares(){
        return this.pastSquares;
    }

    /**
     * Returns the square we last travelled to
     *
     * @return lastSquare
     */
    public Square getLastSquare(){
        return pastSquares.get( pastSquares.size() - 1 );
    }

    /**
     * Adds a square to the list of past squares
     *
     * @param lastSquare
     */
    public void addPastSquare(Square lastSquare){
        this.pastSquares.add(lastSquare);
    }

    /**
     * A method to get the y-coordinate of the our vehicle position.
     *
     * @return the y coordinate
     */
    public double getY() {
        double result;
        synchronized ( lock ) {
            result = y;
        }
        return result;
    }

    /**
     * A method to get the theta of the our vehicle position.
     *
     * @return the theta value
     */
    public double getTheta() {
        double result;
        synchronized ( lock ) {
            result = theta;
        }
        return result;
    }

    /**
     * A method to set the x-coordinate of our vehicle position.
     *
     * @param x the x coordinate to set
     */
    public void setX(double x) {
        synchronized ( lock ) {
            this.x = x;
        }
    }

    /**
     * A method to set the y-coordinate of our vehicle position.
     *
     * @param y the y coordinate to set
     */
    public void setY(double y) {
        synchronized ( lock ) {
            this.y = y;
        }
    }

    /**
     * A method to set the theta of our vehicle position.
     *
     * @param theta the theta value to set
     */
    public void setTheta(double theta) {
        synchronized ( lock ) {
            this.theta = theta;
        }
    }

    /**
     * A method to update the position vector of our vehicle
     *
     * @param position the current position
     * @param update the update boolean
     */
    public void updatePosition(double[] position, boolean[] update) {
        // ensure that the values don't change while the odometer is running
        synchronized (lock) {
            if (update[0])
                position[0] = x;
            if (update[1])
                position[1] = y;
            if (update[2])
                position[2] = ( theta * 360 / ( 2 * Math.PI ) ) % 360;
        }
    }

    /**
     * A method that returns whether our vehicle is correcting its position or not
     *
     * @return whether vehicle is correcting or not
     */
    public boolean isCorrecting() {
        return correcting;
    }

    /**
     * A method to set correcting to true
     *
     * @param correcting the correcting boolean value
     */
    public void setCorrecting(boolean correcting) {
        this.correcting = correcting;
    }

    /**
     * A method that returns our field mapping
     *
     * @return the field mapping object
     */
    public FieldMapper getFieldMapper() {
        return fieldMapper;
    }

    /**
     * A method that returns our current square
     *
     * @return the current square object
     */
    public Square getCurrentSquare() {
        return currentSquare;
    }

    /**
     * A method that returns the square directly east of the current one
     *
     * @return eastSquare or null of it is a wall
     */
    public Square getEastSquare(){
        if ( getCurrentSquare().getSquarePosition()[0] != 11 ) {
            return getFieldMapper().getMapping()[getCurrentSquare().getSquarePosition()[0] + 1][getCurrentSquare().getSquarePosition()[1]];
        } else {
            return null; // wall
        }
    }

    /**
     * A method that returns the square directly west of the current one
     *
     * @return westSquare or null of it is a wall
     */
    public Square getWestSquare(){
        if ( getCurrentSquare().getSquarePosition()[0] != 0 ) {
            return getFieldMapper().getMapping()[getCurrentSquare().getSquarePosition()[0] - 1][getCurrentSquare().getSquarePosition()[1]];
        } else {
            return null; // wall
        }
    }

    /**
     * A method that returns the square directly north of the current one
     *
     * @return northSquare or null of it is a wall
     */
    public Square getNorthSquare(){
        if ( getCurrentSquare().getSquarePosition()[1] != 11 ) {
            return getFieldMapper().getMapping()[getCurrentSquare().getSquarePosition()[0]][getCurrentSquare().getSquarePosition()[1]+1];
        } else {
            return null; // wall
        }
    }

    /**
     * A method that returns the square directly south of the current one
     *
     * @return southSquare or null of it is a wall
     */
    public Square getSouthSquare(){
        if ( getCurrentSquare().getSquarePosition()[1] != 0 ) {
            return getFieldMapper().getMapping()[getCurrentSquare().getSquarePosition()[0]][getCurrentSquare().getSquarePosition()[1]-1];
        } else {
            return null; // wall
        }
    }

    /**
     * A method that sets our current square
     *
     * @param currentSquare a square object
     */
    public void setCurrentSquare(Square currentSquare) {
        this.currentSquare = currentSquare;
    }

    /**
     * A method to determine the current direction that our vehicle is heading towards
     *
     * @return north, south, east, west, or error
     */
    public String getCurrentDirection() {
        if ( getTheta() >= 7*Math.PI/4 && getTheta() < 2*Math.PI ) {
            return "north";
        }
        if ( getTheta() >= 0 && getTheta() < Math.PI/4 ) {
            return "north";
        }
        if ( getTheta() >= Math.PI/4 && getTheta() < 3*Math.PI/4 ) {
            return "east";
        }
        if ( getTheta() >= 3*Math.PI/4 && getTheta() < 5*Math.PI/4 ) {
            return "south";
        }
        if ( getTheta() >= 5*Math.PI/4 && getTheta() < 7*Math.PI/4 ) {
            return "west";
        }
        return "error"; // this should never happen
    }

    /**
     * A method which calculates the theta of the current direction
     *
     * @return currentDirectionTheta
     */
    public double getCurrentDirectionTheta() {
        String currentDirection = getCurrentDirection();
        if ( currentDirection.equals( "north" )) {
            return 0.0;
        }
        if ( currentDirection.equals( "east" ) ) {
            return Math.PI/2;
        }
        if ( currentDirection.equals( "south" ) ) {
            return Math.PI;
        }
        if ( currentDirection.equals( "west" ) ) {
            return 3*Math.PI/2;
        }
        return 0.0; // should never happen
    }

    /**
     * A method to determine whether the current square is adjacent to an input square or not
     *
     * @param square
     * @return isAdjacent
     */
    public boolean isAdjacentSquare( Square square ) {
        if ( square == getNorthSquare() || square == getSouthSquare() || square == getEastSquare() || square == getWestSquare() ) {
            return true;
        }
        return false;
    }
}
