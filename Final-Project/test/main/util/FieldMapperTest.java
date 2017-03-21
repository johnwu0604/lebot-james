package main.controller;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import main.Parameters;
import main.util.FieldMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import main.resource.Constants;

import static org.mockito.Mockito.when;

/**
 * A class to test the logic in the Field Mapper class
 *
 * @author JohnWu
 */
public class FieldMapperTest {

    @Mock
    private EV3LargeRegulatedMotor leftMotor;

    @Mock
    private EV3LargeRegulatedMotor rightMotor;

    @Mock
    private Odometer odometer;

    Parameters parameters;



    FieldMapper fieldMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        navigator = new Navigator( leftMotor, rightMotor, odometer );
    }

    @Test
    public void testCalculateMinAngle() {
        // given
        double deltaX = 90;
        double deltaY = 90;
        when( odometer.getTheta() ).thenReturn( 0.0 );

        // when
        double minAngle = navigator.calculateMinAngle( deltaX, deltaY );

        // then
        Assert.assertEquals( Math.PI/4, minAngle, 0 );
    }

    @Test
    public void testCalculateDistanceToPoint() {
        // given
        double deltaX = 90;
        double deltaY = 90;

        // when
        double distance = navigator.calculateDistanceToPoint( deltaX, deltaY );

        // then
        Assert.assertEquals( 127.0, distance, 1.0 );
    }

    @Test
    public void testConvertAngle() {
        // given
        double angleToRotateTo = (720*Constants.WHEEL_RADIUS)/Constants.TRACK_LENGTH;

        // when
        int tachoCount = navigator.convertAngle( angleToRotateTo );

        // then
        Assert.assertEquals( 360, tachoCount, 0 );
    }

}
