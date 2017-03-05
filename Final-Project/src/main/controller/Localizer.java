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

        try {
            // rotate to the left wall where we will start recording our sensor readings
            rotateToLeftWall();
            odometer.setTheta(0);

            // keep rotating while storing information about each sensor reading
            ArrayList<SensorReading> sensorReadings = rotateAndRecordSensorReadings();

            // find our first and second minimum index
            int firstMinIndex = calculateFirstMinimumIndex(sensorReadings);
            int secondMinIndex = calculateSecondMinimumIndex(sensorReadings, firstMinIndex);

            // turn vehicle to face right
            navigator.turnTo( calculateRemainingAngleToFaceEast( sensorReadings.get( secondMinIndex ) ) );

            // set our real odometer position values
            odometer.setX(calculateStartingX(sensorReadings.get(firstMinIndex), sensorReadings.get(secondMinIndex)));
            odometer.setY(calculateStartingY(sensorReadings.get(firstMinIndex), sensorReadings.get(secondMinIndex)));

            // travel to corner of field
            travelToStartingCorner();

        } catch ( Exception e ) {
            try {
                throw new Exception( "Error: ", e );
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

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
        float distance = sensorData[0]*100 + Constants.FORWARD_SENSOR_DISTANCE;
        return distance > 100 ? 100 : distance;
    }

    private void travelToStartingCorner() {
        if ( corner ==  1 ) {
            odometer.setTheta( Constants.CORNER_ONE_THETA );
            navigator.travelTo( Constants.CORNER_ONE_X, Constants.CORNER_ONE_Y );
        }
        if ( corner ==  2 ) {
            odometer.setTheta( Constants.CORNER_TWO_THETA );
            navigator.travelTo( Constants.CORNER_TWO_X, Constants.CORNER_TWO_Y );
        }
        if ( corner ==  3 ) {
            odometer.setTheta( Constants.CORNER_THREE_THETA );
            navigator.travelTo( Constants.CORNER_THREE_X, Constants.CORNER_THREE_Y );
        }
        if ( corner ==  4 ) {
            odometer.setTheta( Constants.CORNER_FOUR_THETA );
            navigator.travelTo( Constants.CORNER_FOUR_X, Constants.CORNER_FOUR_Y );
        }
    }

    /**
     * A method to rotate robot until first detection of left wall
     */
    private void rotateToLeftWall() {
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
            if ( Math.abs( sumLeft - sumRight ) < 2 ) {
                minimumIndex = i;
                break;
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
        int secondMinimumIndex = -2;
        double secondMinimumIndexAngle = sensorReadings.get( firstMinimumIndex ).getTheta() - Math.PI/2;
        for ( int i=firstMinimumIndex; i<sensorReadings.size(); i++ ) {
            if ( Math.abs( secondMinimumIndexAngle - sensorReadings.get( i ).getTheta() ) < 0.01 ) {
                secondMinimumIndex = i;
                break;
            }
        }
        return secondMinimumIndex;
    }

    /**
     * A method that calculates the x-coordinate of the vehicle's starting position
     *
     * @param firstMinimum
     * @return
     */
    public double calculateStartingX( SensorReading firstMinimum, SensorReading secondMinimum ) {
        if ( corner ==  1 ) {
            return Constants.CORNER_ONE_X - ( Constants.SQUARE_LENGTH - firstMinimum.getDistance() );
        }
        if ( corner ==  2 ) {
            return Constants.CORNER_TWO_X + ( Constants.SQUARE_LENGTH - secondMinimum.getDistance() );
        }
        if ( corner ==  3 ) {
            return Constants.CORNER_THREE_X + ( Constants.SQUARE_LENGTH - firstMinimum.getDistance() );
        }
        if ( corner ==  4 ) {
            return Constants.CORNER_FOUR_X - ( Constants.SQUARE_LENGTH - secondMinimum.getDistance() );
        }
        return 0;
    }

    /**
     * A method that calculates the y-coordinate of the vehicle's starting position
     *
     * @param secondMinimum
     * @return
     */
    public double calculateStartingY( SensorReading firstMinimum, SensorReading secondMinimum ) {
        if ( corner ==  1 ) {
            return Constants.CORNER_ONE_Y - ( Constants.SQUARE_LENGTH - secondMinimum.getDistance() );
        }
        if ( corner ==  2 ) {
            return Constants.CORNER_TWO_Y - ( Constants.SQUARE_LENGTH - firstMinimum.getDistance() );
        }
        if ( corner ==  3 ) {
            return Constants.CORNER_THREE_Y + ( Constants.SQUARE_LENGTH - secondMinimum.getDistance() );
        }
        if ( corner ==  4 ) {
            return Constants.CORNER_FOUR_Y + ( Constants.SQUARE_LENGTH - firstMinimum.getDistance() );
        }
        return 0;
    }

    /**
     * A method that calculate how much more we need to rotate to orient in eastward direction after retrieving sensor data
     *
     * @param secondMinimum
     * @return
     */
    public double calculateRemainingAngleToFaceEast(SensorReading secondMinimum ) {
        return -(Math.PI / 2 - ( secondMinimum.getTheta() - odometer.getTheta() ) );
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
