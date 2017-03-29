package main.controller;

import lejos.hardware.Sound;
import main.object.Square;
import main.object.UltrasonicSensor;
import main.resource.FieldConstants;
import main.resource.RobotConstants;
import main.resource.ThresholdConstants;
import main.resource.TimeConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * A controller class that maps obstacles along the field
 *
 * @author JohnWu
 */
public class ObstacleMapper extends Thread {

    // objects
    private UltrasonicSensor leftSensor;
    private Odometer odometer;

    // variables
    private volatile boolean running = false;

    /**
     * Main constructor class for our obstacle avoider
     *
     * @param leftSensor
     * @param odometer
     */
    public ObstacleMapper( UltrasonicSensor leftSensor, Odometer odometer ) {
        this.leftSensor = leftSensor;
        this.odometer = odometer;
    }

    /**
     * Main thread. Must use method startRunning() to begin execution
     */
    public void run() {
        ArrayList<SensorReading> sensorReadings = new ArrayList<SensorReading>();
        while ( true ) {
            // pause thread if it is not running
            if ( !running ) {
                try { pauseThread(); } catch ( Exception e ) {}
            }
            // map obstacles on the left
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

    /**
     * A method to temporarily pause our thread
     */
    public void pauseThread() throws InterruptedException {
        synchronized (this) {
            while ( !running ) {
                wait();
            }
        }
    }

    /**
     * A method to temporarily stop our thread
     */
    public void stopRunning() {
        synchronized ( this ) {
            running = false;
        }
    }

    /**
     * A method to restart our thread
     */
    public void startRunning() {
        synchronized (this) {
            running = true;
            notifyAll();
        }
    }


    /**
     * A method to determine if our obstacle detection has timed out or not
     *
     * @param startTime
     * @return whether the detection has timed out
     */
    public boolean detectionTimedOut(double startTime ) {
        double currentTime = System.currentTimeMillis();
        return currentTime - startTime < TimeConstants.OBSTACLE_DETECTION_TIME ? false : true;
    }

    /**
     * A method to calculate our average distance based on sensor readings
     *
     * @param sensorReadings
     * @return average distance
     */
    public double calculateAverageDistance( List<SensorReading> sensorReadings ) {
        double sum = 0.0;
        for ( int i = 0; i < sensorReadings.size(); i++ ) {
            sum += sensorReadings.get(i).getDistance();
        }
        return sum / sensorReadings.size();
    }

    /**
     * A method to calculate our average odometer x reading based on sensor readings
     *
     * @param sensorReadings
     * @return average x
     */
    public double calculateAverageX( List<SensorReading> sensorReadings ) {
        double sum = 0.0;
        for ( int i = 0; i < sensorReadings.size(); i++ ) {
            sum += sensorReadings.get(i).getX();
        }
        return sum / sensorReadings.size();
    }

    /**
     * A method to calculate our average odometer y reading based on sensor readings
     *
     * @param sensorReadings
     * @return average y
     */
    public double calculateAverageY( List<SensorReading> sensorReadings ) {
        double sum = 0.0;
        for ( int i = 0; i < sensorReadings.size(); i++ ) {
            sum += sensorReadings.get(i).getY();
        }
        return sum / sensorReadings.size();
    }

    /**
     * A method to update our mapping when an obstacle is detected
     *
     * @param sensorReadings
     */
    public void updateMapping( ArrayList<SensorReading> sensorReadings ) {
        int size = sensorReadings.size();
        // get average sensor reading for left bound
        double averageX1 = calculateAverageX( sensorReadings.subList(0,size/2) );
        double averageY1 = calculateAverageY( sensorReadings.subList(0,size/2) );
        double averageDistance1 = calculateAverageDistance( sensorReadings.subList(0,size/2) );
        // get average sensor reading for right bound
        double averageX2 = calculateAverageX( sensorReadings.subList(size/2,size) );
        double averageY2 = calculateAverageY( sensorReadings.subList(size/2,size) );
        double averageDistance2 = calculateAverageDistance( sensorReadings.subList(size/2,size) );
        // coodinates of obstacles
        double sensorTheta = odometer.getCurrentDirectionTheta() - Math.PI/2;
        double[] coordinates1 = calculateObstaclePosition( averageX1, averageY1, sensorTheta, averageDistance1 );
        double[] coordinates2 = calculateObstaclePosition( averageX2, averageY2, sensorTheta, averageDistance2 );
        // squares of obstacles
        Square obstacle1 = odometer.getFieldMapper().getSquareOfCoordinate( coordinates1[0], coordinates1[1] );
        Square obstacle2 = odometer.getFieldMapper().getSquareOfCoordinate( coordinates2[0], coordinates2[1] );
        // update mapping
        if ( obstacle1 != null && odometer.isAdjacentSquare( obstacle1 ) && !odometer.getFieldMapper().isEdgeSquare( obstacle1 ) ) {
            declareObstacleInMapping( obstacle1 );
            odometer.getNorthSquare(obstacle1).setShootingPriority(0);
        }
        if ( obstacle2 != null && odometer.isAdjacentSquare( obstacle2 ) && !odometer.getFieldMapper().isEdgeSquare( obstacle2 ) ) {
            declareObstacleInMapping( obstacle2 );
            odometer.getNorthSquare(obstacle1).setShootingPriority(0);
        }
    }



    /**
     * A method to mark this obstacle on the mapping
     *
     * @param square
     */
    public void declareObstacleInMapping( Square square ) {
        int x = square.getSquarePosition()[0];
        int y = square.getSquarePosition()[1];
        odometer.getFieldMapper().getMapping()[x][y].setObstacle( true );
        odometer.getFieldMapper().getMapping()[x][y].setAllowed( false );
    }

    /**
     * A method to calculate the x coordinate of an obstacle based on odometer readings
     *
     * @param x
     * @param y
     * @param theta
     * @param distance
     * @return coordinates of obstacle
     */
    public double[] calculateObstaclePosition( double x, double y, double theta, double distance ) {
        double thetaOfSensor = theta - Math.PI/2;
        double[] coordinates = new double[2];
        coordinates[0] = x + distance * ( Math.sin( thetaOfSensor ) );
        coordinates[1] = y + distance * ( Math.cos( thetaOfSensor ) );
        return coordinates;
    }

    /**
     * Calculate the actual coordinate of the sensor
     *
     * @param odometerX
     * @param odometerY
     * @return
     */
    public double[] calculateLeftSensorCoordinate( double odometerX, double odometerY ) {
        String direction = odometer.getCurrentDirection();
        double[] coordinate = new double[2];
        if ( direction.equals( "north" ) ) {
            coordinate[0] = odometerX - RobotConstants.LEFT_US_SENSOR_TO_ROBOT_DISTANCE_HORIZONTAL;
            coordinate[1] = odometerY + RobotConstants.LEFT_US_SENSOR_TO_ROBOT_DISTANCE_VERTICAL;
        }
        if ( direction.equals( "south" ) ) {
            coordinate[0] = odometerX + RobotConstants.LEFT_US_SENSOR_TO_ROBOT_DISTANCE_HORIZONTAL;
            coordinate[1] = odometerY - RobotConstants.LEFT_US_SENSOR_TO_ROBOT_DISTANCE_VERTICAL;
        }
        if ( direction.equals( "east" ) ) {
            coordinate[0] = odometerX + RobotConstants.LEFT_US_SENSOR_TO_ROBOT_DISTANCE_VERTICAL;
            coordinate[1] = odometerY + RobotConstants.LEFT_US_SENSOR_TO_ROBOT_DISTANCE_HORIZONTAL;
        }
        if ( direction.equals( "west" ) ) {
            coordinate[0] = odometerX - RobotConstants.LEFT_US_SENSOR_TO_ROBOT_DISTANCE_VERTICAL;
            coordinate[1] = odometerY - RobotConstants.LEFT_US_SENSOR_TO_ROBOT_DISTANCE_HORIZONTAL;
        }
        return coordinate;
    }


    /**
     * An object which stores all vehicle characteristics at a specific sensor reading
     */
    public static class SensorReading {

        private float distance;
        private double x;
        private double y;
        private double theta;

        public SensorReading( double x, double y, double theta, float distance ) {
            this.distance = distance;
            this.x = x;
            this.y = y;
            this.theta = theta;
        }

        public float getDistance() {
            return distance;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getTheta() {
            return theta;
        }
    }



}
