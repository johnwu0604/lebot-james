package main.controller;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import main.object.LightSensor;
import main.resource.RobotConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

/**
 * A class to test the logic in the Navigator class
 *
 * @author JohnWu
 */
public class NavigatorTest {

    @Mock
    private EV3LargeRegulatedMotor leftMotor;

    @Mock
    private EV3LargeRegulatedMotor rightMotor;

    @Mock
    private ObstacleAvoider obstacleAvoider;

    @Mock
    private Odometer odometer;

    Navigator navigator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        navigator = new Navigator( leftMotor, rightMotor, odometer, obstacleAvoider );
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
        double angleToRotateTo = (720* RobotConstants.WHEEL_RADIUS)/RobotConstants.TRACK_LENGTH;

        // when
        int tachoCount = navigator.convertAngle( angleToRotateTo );

        // then
        Assert.assertEquals( 360, tachoCount, 0 );
    }

}
