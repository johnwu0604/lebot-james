package main.controller;

import lejos.hardware.Sound;
import main.object.Square;
import main.object.UltrasonicSensor;
import main.resource.FieldConstants;
import main.resource.TimeConstants;

/**
 * A controller class that maps obstacles along the field
 */
public class ObstacleAvoider {

    // objects
    private UltrasonicSensor frontSensor;
    private Odometer odometer;
    private Navigator navigator;

    /**
     * Main constructor class for our obstacle avoider
     *
     * @param frontSensor
     * @param odometer
     */
    public ObstacleAvoider( UltrasonicSensor frontSensor, Odometer odometer ) {
        this.frontSensor = frontSensor;
        this.odometer = odometer;
    }

    /**
     * A method to instantiate our navigator in the class
     *
     * @param navigator
     */
    public void setNavigator( Navigator navigator ) {
        this.navigator = navigator;
    }

    /**
     * A method to scan a square for obstacles and returns whether there is an obstacle or not
     *
     * @param square
     * @return whether the move is available
     */
    public boolean scanSquare( Square square ) {
        frontSensor.startRunning();
        double x = square.getCenterCoordinate()[0];
        double y = square.getCenterCoordinate()[1];
        navigator.turnRobot( navigator.calculateMinAngle( x - odometer.getX(), y - odometer.getY() ) );
        if ( scanSlightLeft() ) {
            updateMapping( square );
            return false;
        }
        if ( scanSlightRight() ) {
            updateMapping( square );
            return false;
        }
        frontSensor.stopRunning();
        return true;
    }

    /**
     * A method to scan slightly left for obstacles
     *
     * @return whether there is an obstacle
     */
    public boolean scanSlightLeft() {
        double startTime = System.currentTimeMillis();
        // rotate left
        while ( !scanTimedOut( startTime ) ) {
            navigator.rotateCounterClockwise();
            float distance = frontSensor.getFilteredFrontSensorData();
            if ( distance < 1.2 * FieldConstants.SQUARE_LENGTH ) {
                return true;
            }
        }
        navigator.stop();
        // rotate back
        startTime = System.currentTimeMillis();
        while ( !scanTimedOut( startTime ) ) {
            navigator.rotateClockwise();
        }
        navigator.stop();
        return false;
    }

    /**
     * A method to scan slightly right for obstacles
     *
     * @return whether there is an obstacle
     */
    public boolean scanSlightRight() {
        double startTime = System.currentTimeMillis();
        // rotate left
        while ( !scanTimedOut( startTime ) ) {
            navigator.rotateClockwise();
            float distance = frontSensor.getFilteredFrontSensorData();
            if ( distance < 1.2 * FieldConstants.SQUARE_LENGTH ) {
                return true;
            }
        }
        navigator.stop();
        // rotate back
        startTime = System.currentTimeMillis();
        while ( !scanTimedOut( startTime ) ) {
            navigator.rotateCounterClockwise();
        }
        navigator.stop();
        return false;
    }

    /**
     * A method to determine if obstacle scan has timed out or not
     *
     * @param startTime
     * @return whether thescan has timed out
     */
    public boolean scanTimedOut( double startTime ) {
        double currentTime = System.currentTimeMillis();
        return currentTime - startTime < TimeConstants.OBSTACLE_SCAN_TIME ? false : true;
    }

    /**
     * A method to update our mapping when an obstacle is detected
     *
     * @param square
     */
    public void updateMapping( Square square ) {
        Sound.beep();
        int x = square.getSquarePosition()[0];
        int y = square.getSquarePosition()[1];
        odometer.getFieldMapper().getMapping()[x][y].setAllowed( false );
        odometer.getFieldMapper().getMapping()[x][y].setObstacle( true );
        odometer.getFieldMapper().getMapping()[x][y].setShootingPriority( 0 );
        if ( y + 1 < 12 ) {
            odometer.getFieldMapper().getMapping()[x][y+1].setShootingPriority( 0 );
        }
    }

}