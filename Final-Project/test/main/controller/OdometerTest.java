package main.controller;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import main.resource.RobotConstants;
import main.util.FieldMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * A class to test the logic in the odometer class
 *
 * @author JohnWu
 */
public class OdometerTest {

    @Mock
    private EV3LargeRegulatedMotor leftMotor;

    @Mock
    private EV3LargeRegulatedMotor rightMotor;

    @Mock
    private FieldMapper fieldMapper;

    private Odometer odometer;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        odometer = new Odometer( leftMotor , rightMotor, fieldMapper );
    }

    @Test
    public void testCalculateMotorDisplacement() {
        // given
        int currentTachoCount = 360;
        int prevTachoCount = 0;

        // when

        double displacement = odometer.calculateMotorDisplacement( currentTachoCount , prevTachoCount );
        // then
        Assert.assertEquals( Math.PI*2* RobotConstants.WHEEL_RADIUS , displacement , 0 );
    }

    @Test
    public void testCalculateThetaChange() {
        // given
        double leftMotorDisplacement = 20;
        double rightMotorDisplacement = 20;

        // when
        double thetaChange = odometer.calculateThetaChange( leftMotorDisplacement, rightMotorDisplacement );

        // then
        Assert.assertEquals( 0 , thetaChange , 0 );
    }

    @Test
    public void testCalculateVehicleDisplacement() {
        // given
        double leftMotorDisplacement = 20;
        double rightMotorDisplacement = -20;

        // when
        double vehicleDisplacement = odometer.calculateVehicleDisplacement( leftMotorDisplacement, rightMotorDisplacement );

        // then
        Assert.assertEquals( 0 , vehicleDisplacement, 0 );
    }

    @Test
    public void testCalculateXDisplacement() {
        // given
        double vehicleDisplacement = 20;
        double theta = 0;

        // when
        double deltaX = odometer.calculateXDisplacement( vehicleDisplacement, theta );

        // then
        Assert.assertEquals( 0, deltaX, 0 );
    }

    @Test
    public void testCalculateYDisplacement() {
        // given
        double vehicleDisplacement = 20;
        double theta = 0;

        // when
        double deltaY = odometer.calculateYDisplacement( vehicleDisplacement, theta );

        // then
        Assert.assertEquals( 20, deltaY, 0 );
    }

}
