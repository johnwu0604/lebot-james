package main.controller;

import main.object.Square;
import main.object.UltrasonicSensor;
import main.resource.ThresholdConstants;
import main.resource.TimeConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * A controller class that maps obstacles along the field
 */
public class ObstacleAvoider extends Thread {

    // objects
    private UltrasonicSensor leftSensor;
    private UltrasonicSensor frontSensor;
    private Odometer odometer;

    // variables
    private volatile boolean running;

    /**
     * Main constructor class for our obstacle avoider
     *
     * @param leftSensor
     * @param frontSensor
     * @param odometer
     */
    public ObstacleAvoider( UltrasonicSensor leftSensor, UltrasonicSensor frontSensor, Odometer odometer ) {
        this.leftSensor = leftSensor;
        this.frontSensor = frontSensor;
        this.odometer = odometer;
    }

    /**
     * Our main thread that scans for obstacles on the left while moving
     */
    public void run() {
        while ( true ) {
            ArrayList<SensorReading> sensorReadings = new ArrayList<>();
            while ( running ) {
                if ( leftSensor.getFilteredSensorDataAvoidance() < ThresholdConstants.OBSTACLE_TRACKING ) {
                    double startTime = System.currentTimeMillis();
                    while ( !hasTimedOut( startTime ) ) {
                        SensorReading currentReading = new SensorReading( odometer.getX(),
                                odometer.getY(), odometer.getTheta(), leftSensor.getFilteredSensorDataAvoidance() );
                        sensorReadings.add( currentReading );
                    }
                    if ( calculateAverageDistance( sensorReadings ) < ThresholdConstants.OBSTACLE_TRACKING ) {
                        updateMapping( sensorReadings );
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
     * A method to determine if our obstacle detection has timed out or not
     *
     * @param startTime
     * @return whether the detection has timed out
     */
    public boolean hasTimedOut( double startTime ) {
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
     * A method to calculate our average odometer theta reading based on sensor readings
     *
     * @param sensorReadings
     * @return average theta
     */
    public double calculateAverageTheta( List<SensorReading> sensorReadings ) {
        double sum = 0.0;
        for ( int i = 0; i < sensorReadings.size(); i++ ) {
            sum += sensorReadings.get(i).getTheta();
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
        double averageTheta1 = calculateAverageTheta( sensorReadings.subList(0,size/2) );
        double averageDistance1 = calculateAverageDistance( sensorReadings.subList(0,size/2) );
        // get average sensor reading for right bound
        double averageX2 = calculateAverageX( sensorReadings.subList(size/2,size) );
        double averageY2 = calculateAverageY( sensorReadings.subList(size/2,size) );
        double averageTheta2 = calculateAverageTheta( sensorReadings.subList(size/2,size) );
        double averageDistance2 = calculateAverageDistance( sensorReadings.subList(size/2,size) );
        // coodinates of obstacles
        double[] coordinates1 = calculateObstaclePosition( averageX1, averageY1, averageTheta1, averageDistance1 );
        double[] coordinates2 = calculateObstaclePosition( averageX2, averageY2, averageTheta2, averageDistance2 );
        // squares of obstacles
        Square obstacle1 = odometer.getFieldMapper().getSquareOfCoordinate( coordinates1[0], coordinates1[1] );
        Square obstacle2 = odometer.getFieldMapper().getSquareOfCoordinate( coordinates2[0], coordinates2[1] );
        // update mapping
        if ( obstacle1 != null ) {
            declareObstacleInMapping( obstacle1 );
        }
        if ( obstacle2 != null ) {
            declareObstacleInMapping( obstacle2 );
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
        odometer.getFieldMapper().getMapping()[x][y].setAllowed( true );
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
        if ( thetaOfSensor < 0 ) {
            thetaOfSensor += 2*Math.PI;
        }
        double[] coordinates = new double[2];
        coordinates[0] = x + distance * ( Math.sin( thetaOfSensor ) );
        coordinates[1] = y + distance * ( Math.cos( thetaOfSensor ) );
        return coordinates;
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
