package main.controller;

import lejos.robotics.SampleProvider;
import main.object.UltrasonicSensor;
import main.resource.FieldConstants;
import main.resource.RobotConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

/**
 * A testing unit to test the logic in the localizer class
 */
public class LocalizerTest {

    @Mock
    private Odometer odometer;

    @Mock
    private UltrasonicSensor ultrasonicSensor;

    @Mock
    private Navigator navigator;

    private Localizer localizer1; // corner 1

    private Localizer localizer2; // corner 2

    private Localizer localizer3; // corner 3

    private Localizer localizer4; // corner 4

    private ArrayList<Localizer.SensorReading> sensorReadings;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        localizer1 = new Localizer( odometer, ultrasonicSensor, navigator, 1 );
        localizer2 = new Localizer( odometer, ultrasonicSensor, navigator, 2 );
        localizer3 = new Localizer( odometer, ultrasonicSensor, navigator, 3 );
        localizer4 = new Localizer( odometer, ultrasonicSensor, navigator, 4 );
        sensorReadings = getSensorReadings();
    }

    @Test
    public void testSumDistances() {
        // when
        float sum = localizer1.sumDistances( sensorReadings.subList( 0, 3 ) );
        // then
        Assert.assertEquals( 297, sum, 0 );
    }

    @Test
    public void testCalculateFirstMinimumIndex() {
        // when
        int firstMin = localizer1.calculateFirstMinimumIndex( sensorReadings );
        // then
        Assert.assertEquals( 10, sensorReadings.get(firstMin).getDistance(), 0 );
    }

    @Test
    public void calculateSecondMinimumIndex() {
        // when
        int firstMin = localizer1.calculateFirstMinimumIndex( sensorReadings );
        int secondMin = localizer1.calculateSecondMinimumIndex( sensorReadings, firstMin );
        // then
        Assert.assertEquals( 11, sensorReadings.get(secondMin).getDistance(), 0 );
    }

    @Test
    public void testCalculateStartingX() {
        // given
        Localizer.SensorReading min = new Localizer.SensorReading();
        min.setDistance( 10 );
        double squareLength = FieldConstants.SQUARE_LENGTH - RobotConstants.FRONT_US_SENSOR_TO_TRACK_DISTANCE/4;
        double c14 = 0 - ( squareLength - 10 ); // corner 1 and 4 expected
        double c23 = 10*FieldConstants.SQUARE_LENGTH + ( squareLength - 10 ); // corner 2 and 3 expected

        // when
        double x1 = localizer1.calculateStartingX( min, min );
        double x2 = localizer2.calculateStartingX( min, min );
        double x3 = localizer3.calculateStartingX( min, min );
        double x4 = localizer4.calculateStartingX( min, min );

        // then
        Assert.assertEquals( c14, x1, 0);
        Assert.assertEquals( c23, x2, 0);
        Assert.assertEquals( c23, x3, 0);
        Assert.assertEquals( c14, x4, 0);

    }

    @Test
    public void testCalculateStartingY() {
        // given
        Localizer.SensorReading min = new Localizer.SensorReading();
        min.setDistance( 10 );
        double squareLength = FieldConstants.SQUARE_LENGTH - RobotConstants.FRONT_US_SENSOR_TO_TRACK_DISTANCE/4;
        double c12 = 0 - ( squareLength - 10 ); // corner 1 and 4 expected
        double c34 = 10*FieldConstants.SQUARE_LENGTH + ( squareLength - 10 ); // corner 2 and 3 expected

        // when
        double y1 = localizer1.calculateStartingY( min, min );
        double y2 = localizer2.calculateStartingY( min, min );
        double y3 = localizer3.calculateStartingY( min, min );
        double y4 = localizer4.calculateStartingY( min, min );

        // then
        Assert.assertEquals( c12, y1, 0);
        Assert.assertEquals( c12, y2, 0);
        Assert.assertEquals( c34, y3, 0);
        Assert.assertEquals( c34, y4, 0);

    }

    @Test
    public void testCalculateStartingTheta() {
        // when
        double c1 = localizer1.calculateStartingTheta();
        double c2 = localizer2.calculateStartingTheta();
        double c3 = localizer3.calculateStartingTheta();
        double c4 = localizer4.calculateStartingTheta();

        // then
        Assert.assertEquals( Math.PI/2, c1, 0 );
        Assert.assertEquals( 0, c2, 0 );
        Assert.assertEquals( 3*Math.PI/2, c3, 0 );
        Assert.assertEquals( Math.PI, c4, 0 );
    }

    public ArrayList<Localizer.SensorReading> getSensorReadings() {
        ArrayList<Localizer.SensorReading> sensorReadings = new ArrayList<>();

        for ( int i = 100; i > 10; i-- ) {
            Localizer.SensorReading s = new Localizer.SensorReading();
            s.setTheta( 0 );
            s.setDistance( i );
            sensorReadings.add( s );
        }

        // first minimum at distance = 10, assume theta is pi/4 here
        Localizer.SensorReading min1 = new Localizer.SensorReading();
        min1.setTheta( 7*Math.PI/4 );
        min1.setDistance( 10 );
        sensorReadings.add( min1 );

        for ( int i = 11; i < 100; i++ ) {
            Localizer.SensorReading s = new Localizer.SensorReading();
            s.setTheta( 3*Math.PI/2 );
            s.setDistance( i );
            sensorReadings.add( s );
        }

        // second minimum at distance = 10, assume theta is 3*pi/4 here
        Localizer.SensorReading min2 = new Localizer.SensorReading();
        min2.setTheta( 5*Math.PI/4 );
        min2.setDistance( 11 );
        sensorReadings.add( min2 );

        return sensorReadings;

    }

}
