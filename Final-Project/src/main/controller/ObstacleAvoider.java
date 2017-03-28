package main.controller;

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
     * Our main thread that scans for obstacles on the left while moving
     */
    public void run() {
        while ( true ) {
            ArrayList<SensorReading> sensorReadings = new ArrayList<>();
            while ( running ) {
                if ( leftSensor.getFilteredLeftSensorData() < ThresholdConstants.OBSTACLE_TRACKING ) {
                    int numberTimesAboveThreshold = 0;
                    double startTime = System.currentTimeMillis();
                    while ( !detectionTimedOut( startTime ) ) {
                        float distance = leftSensor.getFilteredLeftSensorData();
                        if ( distance > ThresholdConstants.OBSTACLE_TRACKING ) {
                            numberTimesAboveThreshold ++;
                        }
                        double[] sensorCoordinate = calculateLeftSensorCoordinate( odometer.getX(), odometer.getY() );
                        SensorReading currentReading = new SensorReading( sensorCoordinate[0],
                                sensorCoordinate[1], odometer.getTheta(), distance );
                        sensorReadings.add( currentReading );
                    }
                    if ( numberTimesAboveThreshold < sensorReadings.size()*0.2 ) {
                        if ( calculateAverageDistance( sensorReadings ) < ThresholdConstants.OBSTACLE_TRACKING ) {
                            updateMapping( sensorReadings );
                        }
                    }
                    sensorReadings.clear();
                }
            }
        }
    }

    /**
     * A method to temporarily start our thread
     */
    public void startRunning() {
        this.running = true;
    }

    /**
     * A method to temporarily stop our thread
     */
    public void stopRunning() {
        this.running = false;
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
        int x = square.getSquarePosition()[0];
        int y = square.getSquarePosition()[1];
        odometer.getFieldMapper().getMapping()[x][y].setAllowed( false );
        odometer.getFieldMapper().getMapping()[x][y].setObstacle( true );
    }

}
