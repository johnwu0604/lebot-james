package main.controller;

import lejos.robotics.SampleProvider;
import main.resource.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A controller to localize our robot.
 */
public class Localizer extends Thread {

    // objects
    private Odometer odometer;
    private SampleProvider sensor;
    private Navigator navigator;

    // variables
    private float[] sensorData;
    private int corner;

    /**
     * Our default constructor
     *
     * @param odometer
     * @param sensor
     * @param navigator
     */
    public Localizer( Odometer odometer, SampleProvider sensor, Navigator navigator, int corner ) {
        this.odometer = odometer;
        this.sensor = sensor;
        this.sensorData = new float[sensor.sampleSize()];
        this.navigator = navigator;
        this.corner = corner;
    }

    /**
     * The main method for localizing our robot
     */
    public void run() {
        // rotate to the left wall where we will start recording our sensor readings
        rotateToleftWall();
        odometer.setTheta( 0 );
        // keep rotating while storing information about each sensor reading
        ArrayList<SensorReading> sensorReadings = rotateAndRecordSensorReadings();
        // find our first minimum index
        int firstMinIndex = calculateFirstMinimumIndex( sensorReadings );
        int secondMinIndex = calculateSecondMinimumIndex( sensorReadings, firstMinIndex );

        // turn vehicle to face right
        navigator.turnTo( sensorReadings.get( secondMinIndex ).getTheta() - Math.PI/2 );
        // set our new odometer values
        odometer.setTheta( calculateTheta( corner ) );
        odometer.setX( calculateStartingX( sensorReadings.get( firstMinIndex ), corner ) );
        odometer.setY( calculateStartingY( sensorReadings.get( secondMinIndex ), corner ) );
        // travel to corner of field (hardcoding for corner 0 for now)
        navigator.travelTo( Constants.CORNER_ONE_X, Constants.CORNER_ONE_Y );
    }

    /**
     * A method to rotate our vehicle and characteristics on each sensor reading
     *
     * @return
     */
    public ArrayList<SensorReading> rotateAndRecordSensorReadings() {
        ArrayList<SensorReading> sensorReadings = new ArrayList<>();
        navigator.rotateCounterClockwise();
        while ( getFilteredSensorData() < Constants.LOCALIZATION_WALL_DISTANCE + Constants.LOCALIZATION_NOISE_MARGIN ) {
            SensorReading sensorReading = new SensorReading();
            sensorReading.setDistance( getFilteredSensorData() );
            sensorReading.setTheta( odometer.getTheta() );
            sensorReadings.add( sensorReading );
            try { Thread.sleep( Constants.LOCALIZATION_SENSOR_READING_PERIOD ); } catch( Exception e ){ }
        }
        navigator.stop();
        return sensorReadings;
    }

    /**
     * A method which filters our data for the distance
     *
     * @return
     */
    private float getFilteredSensorData() {
        sensor.fetchSample( sensorData, 0 );
        float distance = sensorData[0]*100;
        return distance > 100 ? 100 : distance;
    }

    /**
     * A method to rotate robot until first detection of left wall
     */
    private void rotateToleftWall() {
        while ( getFilteredSensorData() < Constants.LOCALIZATION_WALL_DISTANCE + Constants.LOCALIZATION_NOISE_MARGIN ) {
            navigator.rotateCounterClockwise();
        }
        while ( getFilteredSensorData() > Constants.LOCALIZATION_WALL_DISTANCE ) {
            navigator.rotateCounterClockwise();
        }
        navigator.stop();
    }

    public float sumDistances( List<SensorReading> sensorReadings ) {
        float sum = 0;
        for ( SensorReading sensorReading : sensorReadings ) {
            sum += sensorReading.getDistance();
        }
        return sum;
    }

    /**
     * A method that returns the index of the reading that corresponds to the first minimum distance
     *
     * @param sensorReadings
     * @return
     */
    public int calculateFirstMinimumIndex(ArrayList<SensorReading> sensorReadings ) {
        int minimumIndex = -1;
        for ( int i=20; i<sensorReadings.size()-30; i++ ) {
            float sumLeft = sumDistances( sensorReadings.subList( i-20, i ) );
            float sumRight = sumDistances( sensorReadings.subList( i+1, i+21 ) );
            if ( Math.abs( sumLeft - sumRight ) < 1 ) {
                minimumIndex = i;
            }
        }
        return minimumIndex;
    }

    /**
     * A method that returns the index of the reading that corresponds to the second minimum distance
     *
     * @param sensorReadings
     * @param firstMinimumIndex
     * @return
     */
    public int calculateSecondMinimumIndex( ArrayList<SensorReading> sensorReadings, int firstMinimumIndex ) {
        int secondMinimumIndex = -1;
        double secondMinimumIndexAngle = sensorReadings.get( firstMinimumIndex ).getTheta() - Math.PI/2;
        for ( int i=firstMinimumIndex; i<sensorReadings.size(); i++ ) {
            if ( Math.abs( secondMinimumIndexAngle - sensorReadings.get( i ).getTheta() ) < 0.1 ) {
                secondMinimumIndex = i;
            }
        }
        return secondMinimumIndex;
    }

    /**
     * A method that calculates the correct theta at the end of localization.
     *
     * @param corner
     * @return
     */
    public double calculateTheta( int corner ) {
        //hardcoding for corner 1 for now
        return Math.PI/2;
    }

    /**
     * A method that calculates the x-coordinate of the vehicle's starting position
     *
     * @param firstMinimum
     * @return
     */
    public double calculateStartingX( SensorReading firstMinimum, int corner ) {
        //hardcoding for corner 1 for now
        return - ( Constants.SQUARE_LENGTH - firstMinimum.getDistance() );
    }

    /**
     * A method that calculates the y-coordinate of the vehicle's starting position
     *
     * @param secondMinimum
     * @return
     */
    public double calculateStartingY( SensorReading secondMinimum, int corner ) {
        //hardcoding for corner 1 for now
        return - ( Constants.SQUARE_LENGTH - secondMinimum.getDistance() );
    }

    /**
     * An object which stores all vehicle characteristics at a specific sensor reading
     */
    public class SensorReading {

        private float distance;
        private double theta;

        public SensorReading() {

        }

        public float getDistance() {
            return distance;
        }

        public void setDistance( float distance ) {
            this.distance = distance;
        }

        public double getTheta() {
            return theta;
        }

        public void setTheta( double theta ) {
            this.theta = theta;
        }
    }

}
