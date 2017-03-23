package main.controller;

import lejos.robotics.SampleProvider;
import main.object.UltrasonicSensor;
import main.resource.ThresholdConstants;
import main.resource.FieldConstants;
import main.resource.RobotConstants;
import main.resource.TimeConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * A controller to localize our robot.
 *
 * @author JohnWu
 */
public class Localizer extends Thread {

    // objects
    private Odometer odometer;
    private UltrasonicSensor ultrasonicSensor;
    private Navigator navigator;

    // variables
    private int corner;


    /**
     * Our default constructor
     *
     * @param odometer odometer controller used in the robot
     * @param ultrasonicSensor front facing ultrasonic sensor object used in the robot
     * @param navigator navigator controller used in the robot
     */
    public Localizer( Odometer odometer, SampleProvider ultrasonicSensor, Navigator navigator, int corner ) {
        this.odometer = odometer;
        this.ultrasonicSensor = new UltrasonicSensor( ultrasonicSensor );
        this.navigator = navigator;
        this.corner = corner;
    }

    /**
     * The main method for localizing our robot
     */
    public void run() {

        try {
            ultrasonicSensor.start();

            int firstMinIndex = -1;
            int secondMinIndex = -2;
            ArrayList<SensorReading> sensorReadings = new ArrayList<SensorReading>();

            // repeatedly rotate until we find can precisely localize
            while ( firstMinIndex == -1 || secondMinIndex == -2 ) {
                try {
                    // rotate to the left wall where we will start recording our sensor readings
                    rotateToLeftWall();
                    odometer.setTheta(0);
                    // keep rotating while storing information about each sensor reading
                    sensorReadings = rotateAndRecordSensorReadings();

                    // find our first and second minimum index
                    firstMinIndex = calculateFirstMinimumIndex(sensorReadings);
                    secondMinIndex = calculateSecondMinimumIndex(sensorReadings, firstMinIndex);
                } catch ( Exception e ) {
                    // should not happen
                }
            }

            ultrasonicSensor.stopRunning();

            // turn vehicle to face east
            navigator.turnTo( calculateRemainingAngleToFaceEast( sensorReadings.get( secondMinIndex ) ) );

            // set our real odometer position values
            odometer.setX(calculateStartingX(sensorReadings.get(firstMinIndex), sensorReadings.get(secondMinIndex)));
            odometer.setY(calculateStartingY(sensorReadings.get(firstMinIndex), sensorReadings.get(secondMinIndex)));
            odometer.setTheta( calculateStartingTheta() );
            setStartingSquare();
            moveToCenterOfSquare();

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
     * @return an ArrayList of sensor reading objects
     */
    public ArrayList<SensorReading> rotateAndRecordSensorReadings() {
        ArrayList<SensorReading> sensorReadings = new ArrayList<SensorReading>();
        navigator.rotateCounterClockwise();
        while ( ultrasonicSensor.getFilteredSensorData() < ThresholdConstants.LOCALIZATION_WALL_DISTANCE + ThresholdConstants.LOCALIZATION_NOISE_MARGIN ) {
            SensorReading sensorReading = new SensorReading();
            sensorReading.setDistance( ultrasonicSensor.getFilteredSensorData() );
            sensorReading.setTheta( odometer.getTheta() );
            sensorReadings.add( sensorReading );
            try { Thread.sleep( TimeConstants.ULTRASONICSENSOR_SENSOR_READING_PERIOD ); } catch( Exception e ){ }
        }
        navigator.stopMotors();
        return sensorReadings;
    }

    /**
     * A method to rotate robot until first detection of left wall
     */
    private void rotateToLeftWall() {
        while ( ultrasonicSensor.getFilteredSensorData() < ThresholdConstants.LOCALIZATION_WALL_DISTANCE + ThresholdConstants.LOCALIZATION_NOISE_MARGIN ) {
            navigator.rotateCounterClockwise();
        }
        while ( ultrasonicSensor.getFilteredSensorData() > ThresholdConstants.LOCALIZATION_WALL_DISTANCE ) {
            navigator.rotateCounterClockwise();
        }
        navigator.stopMotors();
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
     * @param sensorReadings the sensor readings recorded from its rotation
     * @return the index of the sensor reading corresponding to the first minimum
     */
    public int calculateFirstMinimumIndex(ArrayList<SensorReading> sensorReadings ) {
        int minimumIndex = -1;
        for ( int i=20; i<sensorReadings.size()-30; i++ ) {
            float sumLeft = sumDistances( sensorReadings.subList( i-20, i ) );
            float sumRight = sumDistances( sensorReadings.subList( i+1, i+21 ) );
            if ( Math.abs( sumLeft - sumRight ) < 1.5 ) {
                minimumIndex = i;
                break;
            }
        }
        return minimumIndex;
    }

    /**
     * A method that returns the index of the reading that corresponds to the second minimum distance
     *
     * @param sensorReadings the sensor readings recorded from its rotation
     * @param firstMinimumIndex the index of the sensor reading corresponding to the first minimum
     * @return the index of the sensor reading corresponding to the second minimum
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
     * @param firstMinimum the sensor reading object of the first minimum
     * @return the robots starting x-coordinate reading
     */
    public double calculateStartingX( SensorReading firstMinimum, SensorReading secondMinimum ) {
        if ( corner ==  1 ) {
            return FieldConstants.CORNER_ONE_X - ( FieldConstants.SQUARE_LENGTH - firstMinimum.getDistance() - RobotConstants.FRONT_US_SENSOR_TO_TRACK_DISTANCE/4 );
        }
        if ( corner ==  2 ) {
            return FieldConstants.CORNER_TWO_X + ( FieldConstants.SQUARE_LENGTH - secondMinimum.getDistance() - RobotConstants.FRONT_US_SENSOR_TO_TRACK_DISTANCE/4 );
        }
        if ( corner ==  3 ) {
            return FieldConstants.CORNER_THREE_X + ( FieldConstants.SQUARE_LENGTH - firstMinimum.getDistance() - RobotConstants.FRONT_US_SENSOR_TO_TRACK_DISTANCE/4 );
        }
        if ( corner ==  4 ) {
            return FieldConstants.CORNER_FOUR_X - ( FieldConstants.SQUARE_LENGTH - secondMinimum.getDistance() - RobotConstants.FRONT_US_SENSOR_TO_TRACK_DISTANCE/4 );
        }
        return 0;
    }

    /**
     * A method that calculates the y-coordinate of the vehicle's starting position
     *
     * @param secondMinimum the sensor reading object of the second minimum
     * @return the robots starting y-coordinate reading
     */
    public double calculateStartingY( SensorReading firstMinimum, SensorReading secondMinimum ) {
        if ( corner ==  1 ) {
            return FieldConstants.CORNER_ONE_Y - ( FieldConstants.SQUARE_LENGTH - secondMinimum.getDistance() - RobotConstants.FRONT_US_SENSOR_TO_TRACK_DISTANCE/4 );
        }
        if ( corner ==  2 ) {
            return FieldConstants.CORNER_TWO_Y - ( FieldConstants.SQUARE_LENGTH - firstMinimum.getDistance() - RobotConstants.FRONT_US_SENSOR_TO_TRACK_DISTANCE/4 );
        }
        if ( corner ==  3 ) {
            return FieldConstants.CORNER_THREE_Y + ( FieldConstants.SQUARE_LENGTH - secondMinimum.getDistance() - RobotConstants.FRONT_US_SENSOR_TO_TRACK_DISTANCE/4 );
        }
        if ( corner ==  4 ) {
            return FieldConstants.CORNER_FOUR_Y + ( FieldConstants.SQUARE_LENGTH - firstMinimum.getDistance() - RobotConstants.FRONT_US_SENSOR_TO_TRACK_DISTANCE/4 );
        }
        return 0;
    }

    /**
     * A method that calculates the theta of the vehicle's starting position
     *
     * @return the robots starting theta position
     */
    public double calculateStartingTheta() {
        if ( corner ==  1 ) {
            return Math.PI/2;
        }
        if ( corner ==  2 ) {
            return 0;
        }
        if ( corner ==  3 ) {
            return 3*Math.PI/2;
        }
        if ( corner ==  4 ) {
            return Math.PI;
        }
        return 0;
    }

    /**
     * A method which calculates our starting square for field mapping purposes
     *
     * @return
     */
    public void setStartingSquare() {
        if ( corner ==  1 ) {
            odometer.setCurrentSquare( odometer.getFieldMapper().getMapping()[0][0] );
        }
        if ( corner ==  2 ) {
            odometer.setCurrentSquare( odometer.getFieldMapper().getMapping()[0][11] );
        }
        if ( corner ==  3 ) {
            odometer.setCurrentSquare( odometer.getFieldMapper().getMapping()[11][11] );
        }
        if ( corner ==  4 ) {
            odometer.setCurrentSquare( odometer.getFieldMapper().getMapping()[11][0] );
        }
    }

    /**
     * A method to move the robot to the center of the square
     */
    public void moveToCenterOfSquare() {
        double[] centerCoordinate = odometer.getCurrentSquare().getCenterCoordinate();
        double deviationX = centerCoordinate[0] - odometer.getX();
        double deviationY = centerCoordinate[1] - odometer.getY();
        if ( corner == 1 || corner == 3 ) {
            moveToCenterOfSquareX( centerCoordinate, deviationX );
            moveToCenterOfSquareY( centerCoordinate, deviationY );
        }
        if ( corner == 2 || corner == 4 ) {
            moveToCenterOfSquareY( centerCoordinate, deviationY );
            moveToCenterOfSquareX( centerCoordinate, deviationX );
        }
    }

    /**
     * A method to move our robot into the center of the square in the x orientation
     *
     * @param centerCoordinate
     * @param deviationX
     */
    public void moveToCenterOfSquareX( double[] centerCoordinate, double deviationX ) {
        if ( deviationX < 1 ) {
            navigator.travelToXBackward( centerCoordinate[0] );
        }
        if ( deviationX > 1 ) {
            navigator.travelToX( centerCoordinate[0] );
        }
    }

    /**
     * A method to move our robot into the center of the square in the y orientation
     *
     * @param centerCoordinate
     * @param deviationY
     */
    public void moveToCenterOfSquareY( double[] centerCoordinate, double deviationY ) {
        if ( deviationY < 1 ) {
            navigator.travelToYBackward( centerCoordinate[1] );
        }
        if ( deviationY > 1 ) {
            navigator.travelToY( centerCoordinate[1] );
        }
    }



    /**
     * A method that calculate how much more we need to rotate to orient in eastward direction after retrieving sensor data
     *
     * @param secondMinimum the sensor reading object of our second minimum
     * @return the theta value we need to rotate
     */
    public double calculateRemainingAngleToFaceEast(SensorReading secondMinimum ) {
        return -( (Math.PI/2) - ( secondMinimum.getTheta() - odometer.getTheta() ) );
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
